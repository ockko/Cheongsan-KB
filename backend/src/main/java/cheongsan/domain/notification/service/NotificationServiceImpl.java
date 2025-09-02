package cheongsan.domain.notification.service;

import cheongsan.domain.deposit.dto.WeeklyReportDTO;
import cheongsan.domain.notification.dto.CreateNotificationDTO;
import cheongsan.domain.notification.dto.NotificationDTO;
import cheongsan.domain.notification.entity.Notification;
import cheongsan.domain.notification.event.NotificationEvent;
import cheongsan.domain.notification.event.UnreadCountUpdateEvent;
import cheongsan.domain.notification.mapper.NotificationMapper;
import cheongsan.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationServiceImpl implements NotificationService {
    private final NotificationMapper notificationMapper;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final ApplicationEventPublisher eventPublisher; // 이벤트 발행용

    @Override
    public List<NotificationDTO> getNotifications(Long userId) {
        log.debug("알림 목록 조회 시작: userId={}", userId);

        List<Notification> notifications = notificationMapper.findNotificationByUserId(userId);
        List<NotificationDTO> result = notifications.stream()
                .map(NotificationDTO::of)
                .collect(Collectors.toList());

        log.info("알림 목록 조회 완료: userId={}, count={}", userId, result.size());
        return result;
    }

    @Override
    public int getUnreadCount(Long userId) {
        log.debug("안읽은 알림 개수 조회: userId={}", userId);

        int count = notificationMapper.countUnreadNotificationByUserId(userId);

        log.info("안읽은 알림 개수 조회 완료: userId={}, count={}", userId, count);
        return count;
    }

    /**
     * 알림 생성 - 이벤트 기반 웹소켓 전송
     */
    @Override
    @Transactional
    public void createNotification(CreateNotificationDTO notificationDTO) {
        try {
            // 1. 알림 데이터 생성 및 저장
            Notification notification = Notification.builder()
                    .userId(notificationDTO.getUserId())
                    .contents(notificationDTO.getContents())
                    .type(notificationDTO.getType())
                    .isRead(false)
                    .build();

            notificationMapper.save(notification);
            log.info("💾 알림 저장 완료: userId={}, id={}, type={}",
                    notification.getUserId(), notification.getId(), notification.getType());

            // 2. 트랜잭션 완료 후 웹소켓 전송을 위한 이벤트 발행
            NotificationEvent event = new NotificationEvent(
                    this,
                    notification.getUserId(),
                    notification.getContents(),
                    notification.getType(),
                    notification.getId()
            );

            eventPublisher.publishEvent(event);
            log.info("📤 알림 이벤트 발행: userId={}, type={}",
                    notification.getUserId(), notification.getType());

        } catch (Exception e) {
            log.error("알림 생성 실패: {}", notificationDTO, e);
            throw new RuntimeException("알림 생성에 실패했습니다.", e);
        }
    }

    /**
     * 모든 알림 읽음 처리 - 이벤트 기반 웹소켓 전송
     */
    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        try {
            // 1. 모든 알림 읽음 처리
            notificationMapper.markNotificationAsRead(userId);
            log.info("📖 알림 일괄 읽음 처리 완료: userId={}", userId);

            // 2. 읽지 않은 알림 개수 계산
            int unreadCount = notificationMapper.countUnreadNotificationByUserId(userId);

            // 3. 트랜잭션 완료 후 웹소켓 전송을 위한 이벤트 발행
            UnreadCountUpdateEvent event = new UnreadCountUpdateEvent(this, userId, unreadCount);
            eventPublisher.publishEvent(event);

            log.info("📤 읽음 처리 이벤트 발행: userId={}, unreadCount={}", userId, unreadCount);

        } catch (Exception e) {
            log.error("알림 읽음 처리 실패: userId={}", userId, e);
            throw new RuntimeException("알림 읽음 처리에 실패했습니다.", e);
        }
    }

    /**
     * 기존 sendAlarm 메서드 (Deprecated)
     * 기존 코드 호환성을 위해 유지하되, createNotification 사용 권장
     */
    @Override
    @Transactional
    @Deprecated
    public Notification sendAlarm(Notification notification) {
        log.info("⚠️ sendAlarm 메서드는 deprecated됩니다. createNotification을 사용하세요.");

        try {
            // 알림 저장
            notificationMapper.save(notification);

            // 이벤트 발행
            NotificationEvent event = new NotificationEvent(
                    this,
                    notification.getUserId(),
                    notification.getContents(),
                    notification.getType(),
                    notification.getId()
            );

            eventPublisher.publishEvent(event);
            return notification;

        } catch (Exception e) {
            log.error("sendAlarm 실패: ", e);
            throw new RuntimeException("알림 전송에 실패했습니다.", e);
        }
    }

    @Async
    @Override
    public void sendDailyLimitExceededEmail(User user, int dailyLimit, int totalSpent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(user.getEmail());
            helper.setSubject("[티끌모아 청산] 일일 소비 한도 초과 알림");

            Context context = new Context();
            context.setVariable("userNickname", user.getNickname());
            context.setVariable("dailyLimit", dailyLimit);
            context.setVariable("totalSpent", totalSpent);

            String html = templateEngine.process("mail/limitExceeded", context);
            helper.setText(html, true);

            mailSender.send(mimeMessage);
            log.info(user.getEmail() + "님에게 한도 초과 이메일 발송 성공");
        } catch (MessagingException e) {
            log.error(user.getEmail() + "님에게 이메일 발송 실패", e);
            throw new RuntimeException(e);
        }
    }

    @Async
    @Override
    public void sendWeeklyReportEmail(User user, WeeklyReportDTO report) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(user.getEmail());
            helper.setSubject(String.format("[티끌모아 청산] %s님, 지난주 지출 리포트가 도착했어요!", user.getNickname()));

            Context context = new Context();
            context.setVariable("userNickname", user.getNickname());
            context.setVariable("report", report);

            String html = templateEngine.process("mail/weeklyReport", context);
            helper.setText(html, true);

            mailSender.send(mimeMessage);
            log.info(user.getEmail() + "님에게 주간 리포트 이메일 발송 성공");
        } catch (MessagingException e) {
            log.error(user.getEmail() + "님에게 주간 리포트 이메일 발송 실패", e);
            throw new RuntimeException(e);
        }
    }
}

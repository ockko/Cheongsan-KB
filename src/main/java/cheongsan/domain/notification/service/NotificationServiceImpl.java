package cheongsan.domain.notification.service;

import cheongsan.domain.deposit.dto.WeeklyReportDTO;
import cheongsan.domain.notification.dto.CreateNotificationDTO;
import cheongsan.domain.notification.dto.NotificationDTO;
import cheongsan.domain.notification.entity.Notification;
import cheongsan.domain.notification.mapper.NotificationMapper;
import cheongsan.domain.notification.websocket.WebSocketManager;
import cheongsan.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final NotificationMapper notificationMapper;
    private final WebSocketManager webSocketManager;

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

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

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        log.debug("알림 읽음 처리 시작: userId={}", userId);

        try {
            // 1. 모든 알림 읽음 처리
            notificationMapper.markNotificationAsRead(userId);
            log.info("알림 일괄 읽음 처리 완료: userId={}", userId);

            // 2. 읽지 않은 알림 개수 다시 계산
            int unreadCount = notificationMapper.countUnreadNotificationByUserId(userId);

            // 3. WebSocket으로 읽지 않은 알림 수 전송
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "unreadCount");
            payload.put("unreadCount", unreadCount);

            String json = new ObjectMapper().writeValueAsString(payload);
            webSocketManager.sendRawMessageToUser(userId, json);

            log.info("WebSocket으로 unreadCount 전송 완료: userId={}, count={}", userId, unreadCount);

        } catch (Exception e) {
            log.error("알림 읽음 처리 실패: id={}", userId, e);
            throw new RuntimeException("알림 읽음 처리에 실패했습니다.", e);
        }
    }

    @Override
    @Transactional
    public Notification sendAlarm(Notification notification) {
        try {
            // 읽지 않은 알림 개수 조회
            int unreadCount = notificationMapper.countUnreadNotificationByUserId(notification.getUserId());

            // WebSocket 알림 준비
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "notification");
            payload.put("notificationType", notification.getType());
            payload.put("contents", notification.getContents());
            payload.put("unreadCount", unreadCount);

            String json = new ObjectMapper().writeValueAsString(payload);

            // Websocket 메시지 전송
            webSocketManager.sendRawMessageToUser(notification.getUserId(), json);

            log.info("WebSocket으로 알림 전송 완료");
        } catch (Exception e) {
            log.error("알림 WebSocket 전송 실패: ", e);
        }

        return notification;
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

    @Async
    @Override
    public void createNotification(CreateNotificationDTO dto) {
        Notification notification = dto.toEntity();
        notificationMapper.save(notification);
    }
}

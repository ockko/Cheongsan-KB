package cheongsan.domain.notification.service;

import cheongsan.domain.notification.controller.NotificationWebSocketHandler;
import cheongsan.domain.notification.dto.NotificationDTO;
import cheongsan.domain.notification.entity.Notification;
import cheongsan.domain.notification.mapper.NotificationMapper;
import cheongsan.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {
    private final NotificationMapper notificationMapper;
    private final NotificationWebSocketHandler webSocketHandler;

    private final JavaMailSender mailSender;

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
    public void markAsRead(Long userId) {
        log.debug("알림 읽음 처리 시작: id={}", userId);

        try {
            // 1. 알림을 읽음 처리
            notificationMapper.markNotificationAsRead(userId);
            log.info("알림 읽음 처리 완료: id={}", userId);

            // 2. 읽지 않은 알림 개수 다시 계산
            int unreadCount = notificationMapper.countUnreadNotificationByUserId(userId);

            // 3. WebSocket으로 읽지 않은 알림 수 전송
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "unreadCount");
            payload.put("unreadCount", unreadCount);

            String json = new ObjectMapper().writeValueAsString(payload);
            webSocketHandler.sendRawMessageToUser(userId, json);

            log.info("WebSocket으로 unreadCount 전송 완료: userId={}, count={}", userId, unreadCount);

        } catch (Exception e) {
            log.error("알림 읽음 처리 실패: id={}", userId, e);
            throw new RuntimeException("알림 읽음 처리에 실패했습니다.", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void sendDailyLimitExceededEmail(User user, int dailyLimit, int totalSpent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(user.getEmail());
            helper.setSubject("[티끌모아 청산] 일일 소비 한도 초과 알림");

            String htmlContent = String.format(
                    "<h1>안녕하세요, %s님.</h1>" +
                            "<p>오늘의 지출액이 설정하신 일일 한도를 초과하여 알려드립니다.</p>" +
                            "<p><strong>일일 한도:</strong> %,d원</p>" +
                            "<p><strong>오늘 지출액:</strong> %,d원</p>" +
                            "<p>과도한 지출은 상환 목표 달성을 늦출 수 있습니다. 소비 습관을 다시 한번 점검해주세요.</p>",
                    user.getNickname(), dailyLimit, totalSpent
            );
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info(user.getEmail() + "님에게 한도 초과 이메일 발송 성공");
        } catch (MessagingException e) {
            log.error(user.getEmail() + "님에게 이메일 발송 실패", e);
            throw new RuntimeException(e);
        }
    }
}

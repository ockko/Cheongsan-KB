package cheongsan.domain.notification.event;

import cheongsan.domain.notification.mapper.NotificationMapper;
import cheongsan.domain.notification.websocket.WebSocketManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * 알림 이벤트 리스너 (옵저버) - 트랜잭션 완료 후 웹소켓 전송
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class NotificationEventListener {
    private final WebSocketManager webSocketManager;
    private final NotificationMapper notificationMapper;
    private final ObjectMapper objectMapper;

    /**
     * 트랜잭션 커밋 후 알림 웹소켓 전송
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationEvent event) {
        try {
            log.info("🔔 트랜잭션 완료 후 웹소켓 알림 전송: userId={}, type={}",
                    event.getUserId(), event.getType());

            // 현재 읽지 않은 알림 개수 조회
            int unreadCount = notificationMapper.countUnreadNotificationByUserId(event.getUserId());

            // 웹소켓 메시지 구성
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "notification");
            payload.put("notificationType", event.getType());
            payload.put("contents", event.getContents());
            payload.put("unreadCount", unreadCount);
            payload.put("notificationId", event.getNotificationId());
            payload.put("timestamp", System.currentTimeMillis());

            String json = objectMapper.writeValueAsString(payload);

            // 웹소켓 전송
            boolean sent = webSocketManager.sendRawMessageToUser(event.getUserId(), json);

            if (sent) {
                log.info("✅ 웹소켓 알림 전송 성공: userId={}", event.getUserId());
            } else {
                log.warn("⚠️ 웹소켓 알림 전송 실패 (사용자 오프라인): userId={}", event.getUserId());
            }

        } catch (Exception e) {
            log.error("❌ 웹소켓 알림 전송 중 오류: userId={}", event.getUserId(), e);
        }
    }

    /**
     * 읽음 처리 완료 후 unreadCount 업데이트
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUnreadCountUpdateEvent(UnreadCountUpdateEvent event) {
        try {
            log.info("📖 읽음 처리 완료 후 unreadCount 업데이트: userId={}", event.getUserId());

            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "unreadCount");
            payload.put("unreadCount", event.getUnreadCount());
            payload.put("timestamp", System.currentTimeMillis());

            String json = objectMapper.writeValueAsString(payload);
            boolean sent = webSocketManager.sendRawMessageToUser(event.getUserId(), json);

            if (sent) {
                log.info("✅ unreadCount 업데이트 전송 성공: userId={}", event.getUserId());
            }

        } catch (Exception e) {
            log.error("❌ unreadCount 업데이트 전송 중 오류: userId={}", event.getUserId(), e);
        }
    }
}

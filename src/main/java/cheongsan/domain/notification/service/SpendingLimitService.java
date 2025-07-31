package cheongsan.domain.notification.service;

import cheongsan.domain.notification.mapper.NotificationMapper;
import cheongsan.domain.notification.websocket.WebSocketManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class SpendingLimitService {
    private final NotificationMapper notificationMapper;
    private final WebSocketManager webSocketManager;
    ObjectMapper objectMapper = new ObjectMapper();

    public void sendDailyLimitExceededNotification(Long userId, int dailyLimit, BigDecimal todaySpent) {
        String message = String.format("오늘 소비 한도 %d원을 초과했습니다. 현재 지출: %d원", dailyLimit, todaySpent.intValue());

        // 1) 알림 DB 저장
        notificationMapper.insertNotification(userId, message, "DAILY_LIMIT_EXCEEDED");

        // 2) 읽지 않은 알림 개수 조회
        int unreadCount = notificationMapper.countUnreadNotificationByUserId(userId);

        // 3) 웹소켓 메시지 구성
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "notification");
        payload.put("notificationType", "DAILY_LIMIT_EXCEEDED");
        payload.put("contents", message);
        payload.put("unreadCount", unreadCount);

        // 4) 전송
        try {
            String json = objectMapper.writeValueAsString(payload);
            webSocketManager.sendRawMessageToUser(userId, json);
        } catch (Exception e) {
            log.error("웹소켓 메시지 전송 실패: userId={}, error={}", userId, e.getMessage());
        }
    }
}


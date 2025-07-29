package cheongsan.domain.notification.controller;

import cheongsan.domain.notification.dto.NotificationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Log4j2
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, Long> sessionUserMap = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            sessions.put(session.getId(), session);
            sessionUserMap.put(session.getId(), userId);
            log.info("✅ WebSocket 연결 성공: userId={}, sessionId={}", userId, session.getId());

            // 연결 성공 메시지 전송 - 현재 NotificationDto 구조에 맞춤
            NotificationDto welcomeMessage = NotificationDto.builder()
                    .id(null)
                    .contents("🔗 실시간 알림 연결이 완료되었습니다!")
                    .type("general")
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            String message = objectMapper.writeValueAsString(welcomeMessage);
            session.sendMessage(new TextMessage(message));
        } else {
            log.warn("❌ WebSocket 인증 실패: sessionId={}", session.getId());
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        sessionUserMap.remove(session.getId());
        log.info("🔌 WebSocket 연결 종료: sessionId={}", session.getId());
    }

    private Long getUserIdFromSession(WebSocketSession session) {
        // 테스트를 위해 임시로 1L 반환
        return 1L;
    }

    // 전송 메서드
    public void sendRawMessageToUser(Long userId, String message) {
        sessions.entrySet().stream()
                .filter(entry -> userId.equals(sessionUserMap.get(entry.getKey())))
                .forEach(entry -> {
                    try {
                        entry.getValue().sendMessage(new TextMessage(message));
                    } catch (Exception e) {
                        log.error("WebSocket 전송 실패: userId={}", userId, e);
                    }
                });
    }
}

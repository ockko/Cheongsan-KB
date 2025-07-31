package cheongsan.domain.notification.websocket;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@Log4j2
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    WebSocketManager webSocketManager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = extractUserIdFromSession(session);
        if (userId != null) {
            webSocketManager.registerSession(session, userId);
            log.info("✅ WebSocket 연결 성공: userId={}, sessionId={}", userId, session.getId());
        } else {
            log.warn("❌ WebSocket 연결 실패: sessionId={}", session.getId());
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        webSocketManager.unregisterSession(session);
        log.info("🔌 WebSocket 연결 종료: sessionId={}", session.getId());
    }

    private Long extractUserIdFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null && query.startsWith("userId=")) {
            return Long.parseLong(query.split("=")[1]);
        }
        return null;
    }
}

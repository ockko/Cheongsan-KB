package cheongsan.domain.notification.websocket;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Log4j2
public class WebSocketManager {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, Long> sessionUserMap = new ConcurrentHashMap<>();

    public WebSocketSession getSession(Long userId) {
        return sessionUserMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(userId))
                .map(entry -> sessions.get(entry.getKey()))
                .filter(session -> session != null && session.isOpen())
                .findFirst()
                .orElse(null);
    }

    public void registerSession(WebSocketSession session, Long userId) {
        sessions.put(session.getId(), session);
        sessionUserMap.put(session.getId(), userId);
    }

    public void unregisterSession(WebSocketSession session) {
        sessions.remove(session.getId());
        sessionUserMap.remove(session.getId());
    }

    public void sendRawMessageToUser(Long userId, String message) {
        sessions.entrySet().stream()
                .filter(entry -> userId.equals(sessionUserMap.get(entry.getKey())))
                .forEach(entry -> {
                    try {
                        entry.getValue().sendMessage(new TextMessage(message));
                        log.info("웹소켓 메시지 전송 성공: userId={}, sessionId={}", userId, entry.getKey());
                    } catch (Exception e) {
                        log.error("WebSocket 전송 실패: userId={}, sessionId={}", userId, entry.getKey(), e);
                    }
                });
    }
}
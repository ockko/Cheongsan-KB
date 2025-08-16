package cheongsan.domain.notification.websocket;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
    웹소켓 세션 관리 및 실시간 메시지 전송
 */
@Component
@Log4j2
public class WebSocketManager {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>(); // 세션ID로 웹소켓 세션 관리
    private final Map<String, Long> sessionUserMap = new ConcurrentHashMap<>(); // 세션ID <-> 유저 매핑

    // 세션 등록 (기존 세션이 존재하는 경우, 종료 후 등록)
    public void registerSession(WebSocketSession session, Long userId) {
        // 기존 세션이 있다면 제거
        sessionUserMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(userId))
                .map(Map.Entry::getKey)
                .forEach(sessionId -> {
                    WebSocketSession oldSession = sessions.get(sessionId);
                    if (oldSession != null && oldSession.isOpen()) {
                        try {
                            oldSession.close();
                            log.info("기존 WebSocket 세션 종료: userId={}, sessionId={}", userId, sessionId);
                        } catch (IOException e) {
                            log.warn("기존 세션 종료 실패: userId={}, sessionId={}", userId, sessionId, e);
                        } finally {
                            unregisterSession(oldSession);
                        }
                    }
                });

        // 세션 등록
        sessions.put(session.getId(), session);
        sessionUserMap.put(session.getId(), userId);
        log.info("✅ 새 WebSocket 세션 등록: userId={}", userId);
    }

    // 세션 정보 제거
    public void unregisterSession(WebSocketSession session) {
        Long userId = sessionUserMap.get(session.getId());
        sessions.remove(session.getId());
        sessionUserMap.remove(session.getId());
        log.info("🗑️ WebSocket 세션 제거: userId={}", userId);
    }

    // userId에 해당하는 웹소켓 세션 반환 (열려 있는 웹소켓 세션이 없는 경우 null 반환)
    public WebSocketSession getSession(Long userId) {
        return sessionUserMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(userId))
                .map(entry -> sessions.get(entry.getKey()))
                .filter(session -> session != null && session.isOpen())
                .findFirst()
                .orElse(null);
    }

    /**
     * 특정 사용자에게 메시지 전송
     * @return 전송 성공 여부
     */
    public boolean sendRawMessageToUser(Long userId, String message) {
        WebSocketSession session = getSession(userId);

        if (session == null) {
            log.warn("📱 사용자 오프라인: userId={}", userId);
            return false;
        }

        try {
            synchronized (session) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                    log.debug("📤 WebSocket 메시지 전송 성공: userId={}", userId);
                    return true;
                } else {
                    log.warn("🔌 WebSocket 세션이 닫혀있음: userId={}", userId);
                    unregisterSession(session);
                    return false;
                }
            }
        } catch (IOException e) {
            log.error("❌ WebSocket 메시지 전송 실패: userId={}", userId, e);
            unregisterSession(session);
            return false;
        }
    }
}
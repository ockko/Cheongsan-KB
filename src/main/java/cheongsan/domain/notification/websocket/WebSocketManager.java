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
    }

    // 세션 정보 제거
    public void unregisterSession(WebSocketSession session) {
        sessions.remove(session.getId());
        sessionUserMap.remove(session.getId());
        log.info("WebSocket 세션 제거: sessionId={}", session.getId());
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
    
    // userId에 연결된 세션에 JSON 메시지 전송
    public void sendRawMessageToUser(Long userId, String jsonMessage) {
        sessions.entrySet().stream()
                .filter(entry -> userId.equals(sessionUserMap.get(entry.getKey())))
                .forEach(entry -> {
                    WebSocketSession session = entry.getValue();
                    if (session != null && session.isOpen()) {
                        try {
                            session.sendMessage(new TextMessage(jsonMessage));
                            log.info("WebSocket JSON 메시지 전송 성공: userId={}, sessionId={}", userId, entry.getKey());
                        } catch (Exception e) {
                            log.error("WebSocket JSON 메시지 전송 실패: userId={}, sessionId={}", userId, entry.getKey(), e);
                        }
                    }
                });
    }
}
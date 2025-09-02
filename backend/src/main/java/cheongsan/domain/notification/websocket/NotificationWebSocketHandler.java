package cheongsan.domain.notification.websocket;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/*
    WebSocket 연결 및 종료 이벤트 처리 핸들러
    - 클라이언트의 웹소켓 연결 요청 및 종료에 따라, Spring Websocket 컨테이너가 핸들러 자동 호출
    - WebSocketSession 주입 ) Spring이 클라이언트 연결 시 자동 생성함.
 */
@Component
@Log4j2
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    WebSocketManager webSocketManager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            webSocketManager.registerSession(session, userId);
            log.info("✅ WebSocket 연결 성공: userId={}, sessionId={}", userId, session.getId());
        } else {
            log.warn("❌ WebSocket 연결 실패(사용자 식별 불가): sessionId={}", session.getId());
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        webSocketManager.unregisterSession(session);
        log.info("🔌 WebSocket 연결 종료: sessionId={}", session.getId());
    }

    // 추후 JWT 방식으로 수정해야 함.
    private Long getUserIdFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null && query.startsWith("userId=")) {
            return Long.parseLong(query.split("=")[1]);
        }
        return null;
    }
}

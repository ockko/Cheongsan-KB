package cheongsan.domain.notification.websocket;

import cheongsan.domain.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/cheongsan/notify")
public class NotificationTestController {
    private final NotificationService notificationService;
    private WebSocketManager webSocketManager = new WebSocketManager();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/simulate-overlimit/{userId}")
    public ResponseEntity<String> simulateOverLimit(@PathVariable Long userId) {
        notificationService.checkAndNotifyIfOverLimit(userId);
        return ResponseEntity.ok("소비 한도 초과 알림 테스트 완료");
    }
}

package cheongsan.domain.notification.controller;

import cheongsan.domain.notification.dto.NotificationDto;
import cheongsan.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cheongsan/notifications")
@RequiredArgsConstructor
@Log4j2
public class NotificationController {
    private final NotificationService notificationService;

    /**
     * 사용자의 알림 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(
            @RequestParam(required = false, defaultValue = "1") Long userId) {

        log.info("알림 목록 조회 요청 - userId: {}", userId);

        try {
            List<NotificationDto> notifications = notificationService.getNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("알림 목록 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 읽지 않은 알림 개수 조회
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Integer>> getUnreadCount(
            @RequestParam(required = false, defaultValue = "1") Long userId) {

        log.info("읽지 않은 알림 개수 조회 - userId: {}", userId);

        try {
            int unreadCount = notificationService.getUnreadCount(userId);
            Map<String, Integer> response = new HashMap<>();
            response.put("unreadCount", unreadCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("읽지 않은 알림 개수 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 알림 읽음 처리
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, String>> markAsRead(
            @RequestParam(required = false, defaultValue = "1") Long userId) {

        log.info("알림 읽음 처리 요청 - userId: {}", userId);

        try {
            notificationService.markAsRead(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "알림이 읽음 처리되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("알림 읽음 처리 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

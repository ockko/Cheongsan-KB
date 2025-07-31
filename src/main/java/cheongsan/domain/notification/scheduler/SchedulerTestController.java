package cheongsan.domain.notification.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheongsan/test")
public class SchedulerTestController {

    private final NotificationScheduler notificationScheduler;

    // 연체 대출 알림 테스트
    @GetMapping(value = "/run-delinquent-alert", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> runDelinquentAlertManually() {
        notificationScheduler.checkDelinquentLoansAndNotify();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Success!");
        return ResponseEntity.ok(response);
    }

    // 주간 리포트 알림 테스트
    @GetMapping(value = "/run-weekly-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> runWeeklyReportNotificationManually() {
        notificationScheduler.sendWeeklyReportNotification();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Success!");
        return ResponseEntity.ok(response);
    }


}

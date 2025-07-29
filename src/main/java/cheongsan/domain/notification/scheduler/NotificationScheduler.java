package cheongsan.domain.notification.scheduler;

import cheongsan.domain.debt.service.DebtService;
import cheongsan.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

// 알림 스케줄러 추가 (자동 알림 생성)
@Component
@RequiredArgsConstructor
@Log4j2
public class NotificationScheduler {
    private final NotificationService notificationService;
    private final DebtService debtService;

    // 테스트를 위해 임시로 사용할 사용자 ID 목록
    private static final List<Long> TEST_USER_IDS = Arrays.asList(1L, 2L);

    /**
     * 매일 오전 9시에 연체 대출 알림 전송
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDelinquentLoanNotifications() {
        log.info("연체 대출 알림 스케줄러 실행");

//        for (Long userId : TEST_USER_IDS) {
//            try {
//                List<DelinquentLoanResponseDTO> delinquentLoans = debtService.getDelinquentLoans(userId);
//
//                if (!delinquentLoans.isEmpty()) {
//                    for (DelinquentLoanResponseDTO loan : delinquentLoans) {
//                        String message = String.format(
//                                "⚠️ [연체 알림] %s(%s) 대출이 %d일 연체되었습니다. 신속한 상환 부탁드립니다.",
//                                loan.getDebtName(),
//                                loan.getOrganizationName(),
//                                loan.getOverdueDays()
//                        );
//
//                        notificationService.createAndSendNotification(userId, message);
//                    }
//                }
//            } catch (Exception e) {
//                log.error("사용자 {}의 연체 대출 알림 전송 실패", userId, e);
//            }
//        }
    }

    /**
     * 매일 오후 6시에 일일 소비 한도 초과 알림 체크
     * (실제로는 거래 발생 시 실시간으로 체크해야 함)
     */
    @Scheduled(cron = "0 0 18 * * *")
    public void checkDailyLimitExceeded() {
        log.info("일일 소비 한도 체크 스케줄러 실행");

        // TODO: 실제 구현 시 트랜잭션 이벤트와 연동
        // 현재는 스케줄러 구조만 제공
    }

    /**
     * 매주 월요일 오전 10시에 주간 리포트 알림
     */
    @Scheduled(cron = "0 0 10 * * MON")
    public void sendWeeklyReportNotification() {
        log.info("주간 리포트 알림 스케줄러 실행");
    }
}

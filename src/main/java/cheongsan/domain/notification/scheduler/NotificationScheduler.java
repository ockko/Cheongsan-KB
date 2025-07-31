package cheongsan.domain.notification.scheduler;

import cheongsan.domain.debt.dto.DelinquentLoanResponseDTO;
import cheongsan.domain.debt.service.DebtService;
import cheongsan.domain.deposit.service.ReportService;
import cheongsan.domain.notification.dto.CreateNotificationDTO;
import cheongsan.domain.notification.service.NotificationService;
import cheongsan.domain.user.mapper.UserMapper;
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
    private final UserMapper userMapper;
    private final ReportService reportService;

    // 테스트를 위해 임시로 사용할 사용자 ID 목록
    private static final List<Long> TEST_USER_IDS = Arrays.asList(1L, 2L);

    /**
     * 연체 대출 알림 전송 ) 매일 오전 8시
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void checkDelinquentLoansAndNotify() {
        log.info("연체 대출 알림 스케줄러 실행");
        notifyDelinquentLoansForAllUsers();
    }

    public void notifyDelinquentLoansForAllUsers() {
        try {
            List<Long> userIdList = userMapper.getAllUserIds();
            for (Long userId : userIdList) {
                List<DelinquentLoanResponseDTO> delinquentLoans = debtService.getDelinquentLoans(userId);
                log.info("userId: " + userId + " -> 연체대출 " + delinquentLoans.size() + "개 존재");

                if (!delinquentLoans.isEmpty()) {
                    for (DelinquentLoanResponseDTO loan : delinquentLoans) {
                        String contents = String.format("%s/%s 이(가) %d일 연체 중입니다.",
                                loan.getOrganizationName(),
                                loan.getDebtName(),
                                loan.getOverdueDays());

                        String type = "DELINQUENT_LOAN";

                        CreateNotificationDTO request = CreateNotificationDTO.builder()
                                .userId(userId)
                                .contents(contents)
                                .type(type)
                                .build();
                        notificationService.createNotification(request);
                    }
                }
            }
            log.info("연체 대출 알림 완료");
        } catch (Exception e) {
            log.error("연체 대출 알림 중 예외 발생", e);
        }
    }

    /**
     * 주간 리포트 알림 - 매주 월요일 오전 9시
     */
    @Scheduled(cron = "0 0 9 * * Mon")
    public void sendWeeklyReportNotification() {
        log.info("주간 리포트 알림 스케줄러 실행");
        try {
            List<Long> userIdList = userMapper.getAllUserIds();

            for (Long userId : userIdList) {
                // 1. 주간 리포트 생성
//                reportService.createAndSaveLatestWeeklyReport(userId);

                // 2. 이메일 발송
//                notificationService.sendWeeklyReportEmail(userId);

                // 3. 알림 메시지 DB에 저장
                String contents = "주간 리포트가 도착했습니다. 지난 주 소비 흐름을 확인해보세요!";
                String type = "WEEKLY_REPORT";

                CreateNotificationDTO request = CreateNotificationDTO.builder()
                        .userId(userId)
                        .contents(contents)
                        .type(type)
                        .build();

                notificationService.createNotification(request);
            }

            log.info("주간 리포트 알림 스케줄러 완료");

        } catch (Exception e) {
            log.error("주간 리포트 알림 스케줄러 실행 중 예외 발생\n", e);
        }
    }
}

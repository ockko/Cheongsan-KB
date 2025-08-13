//package cheongsan.domain.codef.scheduler;
//
//import cheongsan.domain.codef.service.CodefSyncService;
//import cheongsan.domain.deposit.service.LimitCheckService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//
/// **
// * CODEF API 데이터 동기화 스케줄러
// */
//@Component
//@RequiredArgsConstructor
//@Log4j2
//public class CodefSyncScheduler {
//    private final CodefSyncService codefSyncService;
//    private final LimitCheckService limitCheckService;
//
//
//    // 매시간 정각에 모든 연동된 사용자의 계좌 데이터 동기화
//    @Scheduled(cron = "0 0 * * * *")
//    public void hourlyAccountSync() {
//        log.info("시간별 계좌 데이터 동기화 시작: {}", LocalDateTime.now());
//
//        try {
//            codefSyncService.batchSyncAllUsers();
//            log.info("시간별 계좌 데이터 동기화 완료");
//
//            limitCheckService.checkAllUsersDailyLimitExceeded();
//        } catch (Exception e) {
//            log.error("시간별 계좌 데이터 동기화 실패", e);
//        }
//    }
//}

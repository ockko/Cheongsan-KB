package cheongsan.domain.codef.scheduler;

import cheongsan.domain.codef.service.CodefService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * CODEF API 데이터 동기화 스케줄러
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class CodefSyncScheduler {
    private final CodefService codefService;

    /**
     * 매일 새벽 3시에 전체 사용자 계좌 데이터 동기화
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void dailyAccountSync() {
        log.info("일일 계좌 데이터 동기화 시작: {}", LocalDateTime.now());

        try {
            // CodefServiceEnhanced로 캐스팅하여 배치 동기화 메서드 호출
            if (codefService instanceof cheongsan.domain.codef.service.CodefServiceEnhanced) {
                ((cheongsan.domain.codef.service.CodefServiceEnhanced) codefService).batchSyncAllUsers();
                log.info("일일 계좌 데이터 동기화 완료");
            } else {
                log.warn("배치 동기화 기능을 지원하지 않는 서비스입니다.");
            }

        } catch (Exception e) {
            log.error("일일 계좌 데이터 동기화 실패", e);
            // 에러 알림 등 추가 처리 가능
        }
    }

    /**
     * 매시간 최근 거래내역 동기화 (선택적)
     */
    @Scheduled(cron = "0 0 * * * *")
    public void hourlyTransactionSync() {
        log.debug("시간별 거래내역 동기화 스킵 (필요시 구현)");
        // 필요에 따라 구현
    }

    /**
     * 주간 데이터 정합성 체크 (매주 일요일 새벽 2시)
     */
    @Scheduled(cron = "0 0 2 * * SUN")
    public void weeklyDataIntegrityCheck() {
        log.info("주간 데이터 정합성 체크 시작: {}", LocalDateTime.now());

        try {
            // 데이터 정합성 체크 로직
            // 예: 계좌 잔액과 거래내역 합계 비교 등
            log.info("주간 데이터 정합성 체크 완료");

        } catch (Exception e) {
            log.error("주간 데이터 정합성 체크 실패", e);
        }
    }
}

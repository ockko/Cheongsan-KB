package cheongsan.domain.deposit.service;

import cheongsan.common.config.RootConfig;
import cheongsan.domain.deposit.dto.WeeklyReportDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@Log4j2
class ReportServiceImplTest {

    @Autowired
    private ReportServiceImpl reportService;

    @Test
    @DisplayName("지난주 지출 리포트의 모든 지표를 정확히 계산해야 한다")
    void getLatestWeeklyReport() {
        // given
        Long userId = 1L;

        // when
        WeeklyReportDTO result = reportService.getLatestWeeklyReport(userId);

        // then
        assertNotNull(result);
        assertEquals(12857, result.getAverageDailySpending(), "일일 평균 지출액이 일치해야 합니다.");
        assertEquals(50000, result.getDailyLimit(), "일일 소비 한도가 일치해야 합니다.");
        assertEquals(85.71, result.getAchievementRate(), 0.01, "목표 달성률이 일치해야 합니다."); // 소수점 둘째 자리까지 비교
        assertEquals(10000, result.getSpendingByDay().get("MON"), "월요일 지출액이 일치해야 합니다.");
        assertEquals(0, result.getSpendingByDay().get("THU"), "목요일 지출액은 0이어야 합니다.");
        log.info("정상 사용자 리포트 결과: " + result);
    }

    @Test
    @DisplayName("지난주 지출이 없는 경우, 모든 지표가 0으로 계산되어야 한다")
    void getLatestWeeklyReport_NoSpending() {
        // given
        Long userId = 2L;

        // when
        WeeklyReportDTO result = reportService.getLatestWeeklyReport(userId);

        // then
        assertNotNull(result);
        assertEquals(0, result.getAverageDailySpending(), "평균 지출액은 0이어야 합니다.");
        assertEquals(35000, result.getDailyLimit()); // 일일 한도는 정상 표시
        assertEquals(100.0, result.getAchievementRate(), 0.01, "지출이 없으면 달성률은 100%여야 합니다.");
        log.info("지출 없는 사용자 리포트 결과: " + result);
    }
}
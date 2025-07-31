package cheongsan.domain.deposit.mapper;

import cheongsan.common.config.RootConfig;
import cheongsan.domain.deposit.entity.WeeklyReport;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { RootConfig.class })
@WebAppConfiguration
@Log4j2
class ReportMapperTest {

    @Autowired
    private ReportMapper reportMapper;

    @Test
    @Transactional
    @DisplayName("새로운 주간 리포트를 성공적으로 저장해야 한다")
    void save() {
        // given
        WeeklyReport newReport = WeeklyReport.builder()
                .userId(2L)
                .startDate(LocalDate.of(2025, 7, 28))
                .endDate(LocalDate.of(2025, 8, 3))
                .achievementRate(new BigDecimal("100.00"))
                .dailyLimit(30000)
                .averageDailySpending(28000)
                .spendingByDay("{}")
                .build();

        // when
        reportMapper.save(newReport);

        // then
        assertNotNull(newReport.getId(), "INSERT 후에는 reportId가 생성되어야 합니다.");
        log.info("생성된 리포트 ID: " + newReport.getId());
    }

    @Test
    @DisplayName("특정 사용자의 가장 최근 주간 리포트를 정확히 조회해야 한다")
    void findLatestByUserId() {
        // given
        Long userId = 1L;

        // when
        WeeklyReport latestReport = reportMapper.findLatestByUserId(userId);

        // then
        assertNotNull(latestReport);
        assertEquals(LocalDate.of(2025, 7, 21), latestReport.getStartDate(), "가장 최신 리포트(7월 3주차)가 조회되어야 합니다.");
        log.info("조회된 최신 리포트 시작일: " + latestReport.getStartDate());
    }

    @Test
    @DisplayName("특정 사용자의 모든 과거 리포트 목록을 최신순으로 조회해야 한다")
    void findAllByUserId() {
        // given
        Long userId = 1L;

        // when
        List<WeeklyReport> allReports = reportMapper.findAllByUserId(userId);

        // then
        assertNotNull(allReports);
        assertEquals(2, allReports.size(), "User 1의 리포트는 총 2건이어야 합니다.");
        // ORDER BY DESC 검증
        assertTrue(allReports.get(0).getStartDate().isAfter(allReports.get(1).getStartDate()), "리포트가 최신순으로 정렬되어야 합니다.");
        log.info("조회된 리포트 건수: " + allReports.size());
    }

    @Test
    @DisplayName("특정 날짜가 포함된 주의 리포트를 정확히 조회해야 한다")
    void findByDate() {
        // given
        Long userId = 1L;
        LocalDate dateInWeek = LocalDate.of(2025, 7, 16); // 7월 2주차에 속하는 날짜

        // when
        WeeklyReport report = reportMapper.findByDate(userId, dateInWeek);

        // then
        assertNotNull(report);
        assertEquals(LocalDate.of(2025, 7, 14), report.getStartDate(), "7월 2주차 리포트가 조회되어야 합니다.");
        log.info("조회된 리포트 시작일: " + report.getStartDate());
    }

    @Test
    @DisplayName("해당 날짜에 리포트가 없는 경우 null을 반환해야 한다")
    void findByDate_NoResult() {
        // given
        Long userId = 1L;
        LocalDate dateWithNoReport = LocalDate.of(2025, 7, 1);

        // when
        WeeklyReport report = reportMapper.findByDate(userId, dateWithNoReport);

        // then
        assertNull(report, "해당 날짜에 리포트가 없으면 null을 반환해야 합니다.");
    }
}
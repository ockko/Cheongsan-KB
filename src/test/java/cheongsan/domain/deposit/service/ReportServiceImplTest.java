package cheongsan.domain.deposit.service;

import cheongsan.common.config.RootConfig;
import cheongsan.domain.deposit.dto.WeeklyReportDTO;
import cheongsan.domain.deposit.dto.WeeklyReportHistoryDTO;
import cheongsan.domain.deposit.entity.WeeklyReport;
import cheongsan.domain.deposit.mapper.ReportMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@WebAppConfiguration
@Log4j2
class ReportServiceImplTest {

    @Autowired
    private ReportServiceImpl reportService;

    @Autowired
    private ReportMapper reportMapper;

    @Test
    @Transactional
    @DisplayName("지난주 리포트를 생성하고 DB에 저장해야 한다")
    void createAndSaveLatestWeeklyReport() {
        // given
        Long userId = 2L;
        List<WeeklyReport> beforeSave = reportMapper.findAllByUserId(userId);
        assertEquals(1, beforeSave.size(), "테스트 시작 전 리포트가 1건 있습니다.");

        // when
        WeeklyReportDTO createdReport = reportService.createAndSaveLatestWeeklyReport(userId);

        // then
        assertNotNull(createdReport);
        List<WeeklyReport> afterSave = reportMapper.findAllByUserId(userId);
        assertEquals(2, afterSave.size(), "리포트가 1건 추가되어 총 2건이 되어야 합니다.");
        log.info("신규 생성된 리포트: " + createdReport);
    }

    @Test
    @DisplayName("저장된 가장 최근 리포트를 정확히 조회해야 한다")
    void getLatestReportFromHistory() {
        // given
        Long userId = 1L;

        // when
        WeeklyReportDTO latestReport = reportService.getLatestReportFromHistory(userId);

        // then
        LocalDate expectedStartDate = LocalDate.now().minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        assertNotNull(latestReport);
        assertEquals(expectedStartDate.toString(), latestReport.getStartDate(), "가장 최신 리포트(지난주)가 조회되어야 합니다.");
        log.info("조회된 최신 리포트: " + latestReport);
    }

    @Test
    @DisplayName("특정 날짜가 포함된 주의 리포트를 정확히 조회해야 한다")
    void getReportByDate() {
        // given
        Long userId = 1L;
        LocalDate dateInTwoWeeksAgo = LocalDate.now().minusWeeks(2); // 지지난주

        // when
        WeeklyReportDTO result = reportService.getReportByDate(userId, dateInTwoWeeksAgo);

        // then
        assertNotNull(result);
        assertEquals(dateInTwoWeeksAgo.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toString(), result.getStartDate());
        log.info("날짜로 조회된 리포트: " + result);
    }

    @Test
    @DisplayName("모든 과거 리포트 목록을 DTO로 변환하여 조회해야 한다")
    void getReportHistoryList() {
        // given
        Long userId = 1L;

        // when
        List<WeeklyReportHistoryDTO> historyList = reportService.getReportHistoryList(userId);

        // then
        assertNotNull(historyList);
        assertEquals(2, historyList.size(), "User 1의 리포트 히스토리는 2건이어야 합니다.");
        // 최신순 정렬 확인
        assertTrue(historyList.get(0).getStartDate().compareTo(historyList.get(1).getStartDate()) > 0);
        log.info("조회된 히스토리 목록: " + historyList);
    }
}
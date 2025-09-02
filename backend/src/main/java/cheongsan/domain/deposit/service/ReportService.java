package cheongsan.domain.deposit.service;

import cheongsan.domain.deposit.dto.WeeklyReportDTO;
import cheongsan.domain.deposit.dto.WeeklyReportHistoryDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    WeeklyReportDTO getDashboardWeeklyReport(Long userId);
    WeeklyReportDTO getReportByDate(Long userId, LocalDate date);
    List<WeeklyReportHistoryDTO> getReportHistoryList(Long userId);
}

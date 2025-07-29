package cheongsan.domain.deposit.service;

import cheongsan.domain.deposit.dto.WeeklyReportDTO;

public interface ReportService {

    WeeklyReportDTO getLatestWeeklyReport(Long userId);
}

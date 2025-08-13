package cheongsan.domain.deposit.service;

import cheongsan.domain.deposit.dto.WeeklyReportDTO;

public interface ReportCreationService {
    WeeklyReportDTO createAndSaveLatestWeeklyReport(Long userId);
}

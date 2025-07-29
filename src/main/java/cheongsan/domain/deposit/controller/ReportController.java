package cheongsan.domain.deposit.controller;

import cheongsan.domain.deposit.dto.WeeklyReportDTO;
import cheongsan.domain.deposit.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheongsan/dashboard/reports/weekly")
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<WeeklyReportDTO> getLatestWeeklyReport() {
        Long userId = 1L;

        WeeklyReportDTO result = reportService.getLatestWeeklyReport(userId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

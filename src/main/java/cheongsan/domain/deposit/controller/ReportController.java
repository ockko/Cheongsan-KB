package cheongsan.domain.deposit.controller;

import cheongsan.domain.deposit.dto.WeeklyReportDTO;
import cheongsan.domain.deposit.dto.WeeklyReportHistoryDTO;
import cheongsan.domain.deposit.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheongsan/dashboard/reports/weekly")
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/latest")
    public ResponseEntity<WeeklyReportDTO> getLatestWeeklyReport() {
        Long userId = 1L;
        WeeklyReportDTO result = reportService.getLatestReportFromHistory(userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<WeeklyReportDTO> getWeeklyReportByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long userId = 1L;
        WeeklyReportDTO report = reportService.getReportByDate(userId, date);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @GetMapping("/history-list")
    public ResponseEntity<List<WeeklyReportHistoryDTO>> getWeeklyReportHistoryList() {
        Long userId = 1L;
        List<WeeklyReportHistoryDTO> historyList = reportService.getReportHistoryList(userId);
        return new ResponseEntity<>(historyList, HttpStatus.OK);
    }
}

package cheongsan.domain.deposit.controller;

import cheongsan.common.constant.ResponseMessage;
import cheongsan.common.exception.ResponseDTO;
import cheongsan.domain.deposit.dto.WeeklyReportDTO;
import cheongsan.domain.deposit.dto.WeeklyReportHistoryDTO;
import cheongsan.domain.deposit.service.ReportService;
import cheongsan.domain.user.entity.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheongsan/dashboard/reports/weekly")
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestWeeklyReport(Principal principal) {
        Authentication authentication = (Authentication) principal;
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long userId = customUser.getUser().getId();
        WeeklyReportDTO result = reportService.getDashboardWeeklyReport(userId);
        if (result == null) {
            return new ResponseEntity<>(ResponseMessage.REPORT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<WeeklyReportDTO> getWeeklyReportByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, Principal principal) {
        Authentication authentication = (Authentication) principal;
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long userId = customUser.getUser().getId();
        WeeklyReportDTO report = reportService.getReportByDate(userId, date);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @GetMapping("/history-list")
    public ResponseEntity<List<WeeklyReportHistoryDTO>> getWeeklyReportHistoryList(Principal principal) {
        Authentication authentication = (Authentication) principal;
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long userId = customUser.getUser().getId();
        List<WeeklyReportHistoryDTO> historyList = reportService.getReportHistoryList(userId);
        return new ResponseEntity<>(historyList, HttpStatus.OK);
    }
}

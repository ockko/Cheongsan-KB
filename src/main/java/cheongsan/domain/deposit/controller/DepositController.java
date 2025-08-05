package cheongsan.domain.deposit.controller;

import cheongsan.domain.deposit.dto.DailySpendingDTO;
import cheongsan.domain.deposit.dto.DailyTransactionDTO;
import cheongsan.domain.deposit.dto.MonthlyTransactionDTO;
import cheongsan.domain.deposit.service.DepositService;
import cheongsan.domain.user.entity.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/cheongsan")
@RequiredArgsConstructor
@Log4j2
public class DepositController {
    private final DepositService depositService;

    @GetMapping("/calendar/transactions")
    public ResponseEntity<List<MonthlyTransactionDTO>> getMonthlyTransactions(
            @RequestParam int year,
            @RequestParam int month) {

        log.info("월별 거래 내역 조회 요청 - year: {}, month: {}", year, month);

        List<MonthlyTransactionDTO> transactions = depositService.getMonthlyTransactions(year, month);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/calendar/transactions/{date}")
    public ResponseEntity<List<DailyTransactionDTO>> getDailyTransactions(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        log.info("일별 거래 내역 조회 요청 - date: {}", date);

        List<DailyTransactionDTO> transactions = depositService.getDailyTransactions(date);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/dashboard/daily-spending")
    public ResponseEntity<DailySpendingDTO> getDailySpendingStatus(Principal principal) {
        Authentication authentication = (Authentication) principal;
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long userId = customUser.getUser().getId();
        DailySpendingDTO result = depositService.getDailySpendingStatus(userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

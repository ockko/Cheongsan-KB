package cheongsan.domain.deposit.controller;

import cheongsan.domain.deposit.dto.DailySpendingDTO;
import cheongsan.domain.deposit.dto.DailyTransactionDTO;
import cheongsan.domain.deposit.dto.MonthlyTransactionDTO;
import cheongsan.domain.deposit.service.DailySpendingService;
import cheongsan.domain.deposit.service.DepositService;
import cheongsan.domain.user.entity.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final DailySpendingService dailySpendingService;

    @GetMapping("/calendar/transactions")
    public ResponseEntity<List<MonthlyTransactionDTO>> getMonthlyTransactions(
            @RequestParam int year,
            @RequestParam int month,
            @AuthenticationPrincipal CustomUser customUser) {

        log.info("월별 거래 내역 조회 요청 - year: {}, month: {}", year, month);

        // JWT에서 userId 추출
        Long userId = customUser.getUser().getId();

        // 월 유효성 검증
        if (month < 1 || month > 12) {
            log.warn("잘못된 월 파라미터: {}", month);
            return ResponseEntity.badRequest().build();
        }

        try {
            List<MonthlyTransactionDTO> transactions = depositService.getMonthlyTransactions(userId, year, month);
            log.info("월별 거래 내역 조회 완료 - userId: {}, 결과 수: {}", userId, transactions.size());
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("월별 거래 내역 조회 중 오류 발생 - userId: {}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/calendar/transactions/{date}")
    public ResponseEntity<List<DailyTransactionDTO>> getDailyTransactions(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @AuthenticationPrincipal CustomUser customUser) {

        log.info("일별 거래 내역 조회 요청 - date: {}", date);

        // JWT에서 userId 추출
        Long userId = customUser.getUser().getId();

        try {
            List<DailyTransactionDTO> transactions = depositService.getDailyTransactions(userId, date);
            log.info("일별 거래 내역 조회 완료 - userId: {}, 결과 수: {}", userId, transactions.size());
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("일별 거래 내역 조회 중 오류 발생 - userId: {}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/dashboard/daily-spending")
    public ResponseEntity<DailySpendingDTO> getDailySpendingStatus(Principal principal) {
        Authentication authentication = (Authentication) principal;
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long userId = customUser.getUser().getId();
        DailySpendingDTO result = dailySpendingService.getDailySpendingStatus(userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

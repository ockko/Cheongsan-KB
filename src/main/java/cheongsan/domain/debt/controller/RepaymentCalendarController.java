package cheongsan.domain.debt.controller;

import cheongsan.domain.debt.dto.DailyRepaymentDTO;
import cheongsan.domain.debt.dto.RepaymentCalendarDTO;
import cheongsan.domain.debt.service.DebtService;
import cheongsan.domain.user.entity.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/cheongsan/calendar")
@RequiredArgsConstructor
@Log4j2
public class RepaymentCalendarController {
    private final DebtService debtService;

    // 월별 대출 상환일자 조회
    @GetMapping("/repayments")
    public ResponseEntity<List<RepaymentCalendarDTO>> getMonthlyRepayments(
            @RequestParam int year,
            @RequestParam int month,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        log.info("월별 상환일자 조회 요청 - year: {}, month: {}", year, month);

        Long userId = customUser.getUser().getId();

        try {
            // 월 유효성 검증
            if (month < 1 || month > 12) {
                log.warn("잘못된 월 파라미터: {}", month);
                return ResponseEntity.badRequest().build();
            }

            List<RepaymentCalendarDTO> repayments = debtService.getMonthlyRepayments(userId, year, month);

            log.info("월별 상환일자 조회 완료 - userId: {}, 결과 수: {}", userId, repayments.size());
            return ResponseEntity.ok(repayments);

        } catch (Exception e) {
            log.error("월별 상환일자 조회 중 오류 발생 - userId: {}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // 일별 대출 상환일자 조회
    @GetMapping("/repayments/{date}")
    public ResponseEntity<List<DailyRepaymentDTO>> getDailyRepayments(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        log.info("일별 상환일자 조회 요청 - date: {}", date);

        // JWT에서 userId 추출
        Long userId = customUser.getUser().getId();

        try {
            List<DailyRepaymentDTO> repayments = debtService.getDailyRepayments(userId, date);

            log.info("일별 상환일자 조회 완료 - userId: {}, 결과 수: {}", userId, repayments.size());
            return ResponseEntity.ok(repayments);

        } catch (Exception e) {
            log.error("일별 상환일자 조회 중 오류 발생 - userId: {}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

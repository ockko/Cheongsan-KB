package cheongsan.domain.simulator.controller;

import cheongsan.common.util.LoanCalculator;
import cheongsan.domain.simulator.dto.LoanRecommendationRequestDTO;
import cheongsan.domain.simulator.dto.RecommendationResultDTO;
import cheongsan.domain.simulator.service.DsrCalculator;
import cheongsan.domain.simulator.service.LoanRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheongsan/simulation")
public class RecommendController {

    private final LoanRecommendationService recommendationService;
    private final LoanCalculator loanCalculator;

    @PostMapping("/recommendation")
    public ResponseEntity<?> recommend(@RequestBody LoanRecommendationRequestDTO request) {

        // 1) 기간 환산(년 -> 개월)
        long months = request.getTerm() * 12L;
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(months);

        // 2) 월 상환액 (필요 시 상환방식 매핑 사용)
        BigDecimal monthlyRepayment = loanCalculator.calculateMonthlyPayment(
                LoanCalculator.RepaymentMethod.EQUAL_PRINCIPAL_INTEREST, // 필요하면 Mapper로 변경
                request.getPrincipal(),
                request.getPrincipal(),
                request.getInterestRate(), // 연이율(%)
                start,
                end
        );

        // 3) DSR 계산(간단식: 기존대출 미반영 버전)
        BigDecimal dsr = DsrCalculator.calculateDsr(
                monthlyRepayment,
                request.getAnnualIncome(),
                loanCalculator
        );

        // 4) 추천 호출 (★ DSR 주입)
        RecommendationResultDTO rec = recommendationService.recommendLoans(request, monthlyRepayment, dsr);

        if (rec.getItems().isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "message", "입력하신 조건으로는 현재 대출이 어렵습니다. 연소득 대비 월 상환액을 조정해 주세요.",
                    "dsr", dsr
            ));
        }
        return ResponseEntity.ok(rec);
    }
}

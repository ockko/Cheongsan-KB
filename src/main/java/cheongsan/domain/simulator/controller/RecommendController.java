package cheongsan.domain.simulator.controller;

import cheongsan.common.util.LoanCalculator;
import cheongsan.domain.simulator.dto.LoanProductDTO;
import cheongsan.domain.simulator.dto.LoanRecommendationRequestDTO;
import cheongsan.domain.simulator.service.LoanRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheongsan/simulation")
public class RecommendController {

    private final LoanRecommendationService recommendationService;
    private final LoanCalculator loanCalculator;

    @PostMapping("/recommendation")
    public ResponseEntity<?> recommend(@RequestBody LoanRecommendationRequestDTO request) {
        // 월 상환액 계산
        BigDecimal monthlyRepayment = loanCalculator.calculateMonthlyPayment(
                LoanCalculator.RepaymentMethod.EQUAL_PRINCIPAL_INTEREST,
                request.getPrincipal(),
                request.getPrincipal(),
                request.getInterestRate(),
                LocalDate.now(),
                LocalDate.now().plusMonths(request.getTerm())
        );

        // 추천 서비스 호출
        List<LoanProductDTO> results = recommendationService.recommendLoans(request, monthlyRepayment);

        if (results.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "message", "입력하신 조건으로는 현재 대출이 어렵습니다. 연소득 대비 월 상환액을 조정해 주세요."
            ));
        }
        return ResponseEntity.ok(results);
    }
}

package cheongsan.domain.simulator.controller;

import cheongsan.common.util.LoanCalculator;
import cheongsan.common.util.RepaymentTypeMapper;
import cheongsan.domain.simulator.dto.*;
import cheongsan.domain.simulator.service.LoanRecommendationService;
import cheongsan.domain.simulator.service.LoanSimulationService;
import cheongsan.domain.user.entity.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheongsan/simulation")
public class LoanSimulatorController {

    private final LoanSimulationService loanSimulationService;
    private final LoanRecommendationService loanRecommendationService;
    private final LoanCalculator loanCalculator;

    @PostMapping("/loan")
    public ResponseEntity<LoanResultDTO> analyzeAndRecommend(
            @AuthenticationPrincipal CustomUser customUser,
            @RequestBody LoanAnalyzeRequestDTO request) {

        Long userId = customUser.getUser().getId();

        // 1) 분석
        LoanAnalyzeResponseDTO analysis = loanSimulationService.analyze(request, userId);

        // 1-1) ★ DSR(0~1) → 퍼센트로 변환(소수점 첫째자리)
        BigDecimal dsrPercent = analysis.getDsr()
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);

        // 2) 상환 방식 매핑
        LoanCalculator.RepaymentMethod repaymentMethod =
                RepaymentTypeMapper.toMethod(request.getRepaymentType());

        // 3) 신규 대출 월 상환액 계산 (기간: 년 → 개월)
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(request.getLoanPeriod() * 12L);

        BigDecimal monthlyRepayment = loanCalculator.calculateMonthlyPayment(
                repaymentMethod,
                request.getLoanAmount(),
                request.getLoanAmount(),
                request.getInterestRate(),
                startDate,
                endDate
        );

        // 4) 추천 입력
        LoanRecommendationRequestDTO recommendationInput = new LoanRecommendationRequestDTO();
        recommendationInput.setPrincipal(request.getLoanAmount());
        recommendationInput.setInterestRate(request.getInterestRate());
        recommendationInput.setAnnualIncome(request.getAnnualIncome());
        recommendationInput.setTerm((int) request.getLoanPeriod()); // 년 단위
        recommendationInput.setRepaymentType(request.getRepaymentType().name());

        // 5) 추천
        RecommendationResultDTO rec =
                loanRecommendationService.recommendLoans(recommendationInput, monthlyRepayment, analysis.getDsr());

        // 6) 그래프
        List<GraphDTO> repaymentGraph = List.of(
                new GraphDTO("기존 상환액", analysis.getTotalComparison().getOriginalTotal()),
                new GraphDTO("신규 포함 상환액", analysis.getTotalComparison().getNewLoanTotal())
        );
        List<GraphDTO> interestGraph = List.of(
                new GraphDTO("기존 이자 총액", analysis.getInterestComparison().getExistingInterest()),
                new GraphDTO("신규 포함 이자 총액", analysis.getInterestComparison().getNewLoanInterest())
        );

        // 7) 응답 (★ dsr에 퍼센트 값 넣기)
        LoanResultDTO result = new LoanResultDTO(
                analysis.getTotalComparison(),
                analysis.getInterestComparison(),
                rec.getItems(),
                repaymentGraph,
                interestGraph,
                dsrPercent
        );

        return ResponseEntity.ok(result);
    }
}

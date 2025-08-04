package cheongsan.domain.simulator.controller;

import cheongsan.common.util.LoanCalculator;
import cheongsan.domain.simulator.dto.*;
import cheongsan.domain.simulator.service.LoanRecommendationService;
import cheongsan.domain.simulator.service.LoanSimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
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
    public ResponseEntity<LoanResultDTO> analyzeAndRecommend(@RequestBody LoanAnalyzeRequestDTO request) {

        // 1. 영향 분석
        LoanAnalyzeResponseDTO analysis = loanSimulationService.analyze(request);

        // 2. 상환 방식 변환
        LoanCalculator.RepaymentMethod repaymentMethod;
        switch (request.getRepaymentType()) {
            case EQUAL_PAYMENT:
                repaymentMethod = LoanCalculator.RepaymentMethod.EQUAL_PRINCIPAL_INTEREST;
                break;
            case EQUAL_PRINCIPAL:
                repaymentMethod = LoanCalculator.RepaymentMethod.EQUAL_PRINCIPAL;
                break;
            case LUMP_SUM:
                repaymentMethod = LoanCalculator.RepaymentMethod.BULLET_REPAYMENT;
                break;
            default:
                throw new IllegalArgumentException("알 수 없는 상환 방식: " + request.getRepaymentType());
        }

        // 3. 월 상환액 계산
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(request.getLoanPeriod());

        BigDecimal monthlyRepayment = loanCalculator.calculateMonthlyPayment(
                repaymentMethod,
                request.getLoanAmount(),
                request.getLoanAmount(),
                request.getInterestRate(),
                startDate,
                endDate
        );
        System.out.println("📌 [월 상환액] 입력값 기준 계산된 월 상환액: " + monthlyRepayment);

        // 4. 추천 요청 DTO 구성
        LoanRecommendationRequestDTO recommendationInput = new LoanRecommendationRequestDTO();
        recommendationInput.setPrincipal(request.getLoanAmount());
        recommendationInput.setInterestRate(request.getInterestRate());
        recommendationInput.setAnnualIncome(request.getAnnualIncome());
        recommendationInput.setTerm((int) request.getLoanPeriod());
        recommendationInput.setRepaymentType(request.getRepaymentType().name());

        // 5. 추천 서비스 호출
        List<LoanProductDTO> recommendations = loanRecommendationService.recommendLoans(recommendationInput, monthlyRepayment);

        // 6. 그래프 구성
        List<GraphDTO> repaymentGraph = List.of(
                new GraphDTO("기존 상환액", analysis.getTotalComparison().getOriginalTotal()),
                new GraphDTO("신규 포함 상환액", analysis.getTotalComparison().getNewLoanTotal())
        );
        List<GraphDTO> interestGraph = List.of(
                new GraphDTO("기존 이자 총액", analysis.getInterestComparison().getExistingInterest()),
                new GraphDTO("신규 포함 이자 총액", analysis.getInterestComparison().getNewLoanInterest())
        );
        List<GraphDTO> debtGraph = List.of(
                new GraphDTO("기존 부채비율", analysis.getDebtRatioComparison().getExistingDebtRatio()),
                new GraphDTO("신규 부채비율", analysis.getDebtRatioComparison().getNewDebtRatio())
        );

        // 7. 통합 결과 반환
        LoanResultDTO result = new LoanResultDTO(
                analysis.getTotalComparison(),
                analysis.getInterestComparison(),
                analysis.getDebtRatioComparison(),
                recommendations,
                repaymentGraph,
                interestGraph,
                debtGraph
        );

        return ResponseEntity.ok(result);
    }
}

package cheongsan.domain.simulator.controller;

import cheongsan.common.util.LoanCalculator;
import cheongsan.common.util.RepaymentTypeMapper;
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
        // 1. 사용자 대출 분석
        LoanAnalyzeResponseDTO analysis = loanSimulationService.analyze(request);

        // 2. 상환 방식 매핑
        LoanCalculator.RepaymentMethod repaymentMethod =
                RepaymentTypeMapper.toMethod(request.getRepaymentType());

        // 3. 신규 대출 월 상환액 계산
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

        // 4. 추천 요청 DTO 구성
        LoanRecommendationRequestDTO recommendationInput = new LoanRecommendationRequestDTO();
        recommendationInput.setPrincipal(request.getLoanAmount());
        recommendationInput.setInterestRate(request.getInterestRate());
        recommendationInput.setAnnualIncome(request.getAnnualIncome());
        recommendationInput.setTerm((int) request.getLoanPeriod());
        recommendationInput.setRepaymentType(request.getRepaymentType().name());

        // 5. 대출 상품 추천
        List<LoanProductDTO> recommendations = loanRecommendationService.recommendLoans(recommendationInput, monthlyRepayment);

        // 6. 그래프 데이터 구성
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

        // 7. 응답 반환
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

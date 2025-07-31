package cheongsan.domain.simulator.controller;

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
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheongsan/simulation")
public class LoanSimulatorController {

    private final LoanSimulationService loanSimulationService;
    private final LoanRecommendationService loanRecommendationService;

    @PostMapping("/loan")
    public ResponseEntity<LoanResultDTO> analyzeAndRecommend(@RequestBody LoanAnalyzeRequestDTO request) {

        // 1. 영향 분석 수행
        LoanAnalyzeResponseDTO analysis = loanSimulationService.analyze(request);

        // 2. 사용자 입력값 기반 월 상환금 사용
        BigDecimal MonthlyRepayment = request.getMonthlyRepayment();

        // 3. 추천 요청 DTO 구성
        LoanRecommendationRequestDTO recommendationInput = new LoanRecommendationRequestDTO();
        recommendationInput.setPrincipal(request.getLoanAmount());
        recommendationInput.setInterestRate(request.getInterestRate());
        recommendationInput.setAnnualIncome(request.getAnnualIncome());
        recommendationInput.setTerm((int) request.getLoanPeriod());
        recommendationInput.setRepaymentType(request.getRepaymentType().name());
        recommendationInput.setMonthlyRepayment(MonthlyRepayment);

        // 4. 대출 추천 로직 실행
        List<LoanProductDTO> recommendations = loanRecommendationService.recommendLoans(recommendationInput);

        // 5. 그래프 데이터 구성
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

        // 6. 결과 통합 DTO 생성
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

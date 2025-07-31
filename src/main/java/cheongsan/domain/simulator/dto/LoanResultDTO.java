package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LoanResultDTO {
    private TotalComparisonResultDTO totalComparison;
    private InterestComparisonResultDTO interestComparison;
    private DebtRatioComparisonResultDTO debtRatioComparison;
    private List<LoanProductDTO> recommendedLoans;

    private List<GraphDTO> repaymentGraph;  // 총 상환액 비교
    private List<GraphDTO> interestGraph;   // 총 이자 비용 비교
    private List<GraphDTO> debtGraph;       // 부채 비율 비교
}

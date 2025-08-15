package cheongsan.domain.simulator.dto;

import cheongsan.domain.policy.dto.MonthlyInterestComparisonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class LoanResultDTO {
    private TotalComparisonResultDTO totalComparison;
    private InterestComparisonResultDTO interestComparison;
    private List<LoanProductDTO> recommendedLoans;
    private List<GraphDTO> repaymentGraph;
    private List<MonthlyInterestComparisonDTO> monthlyInterestComparison;
    private BigDecimal dsr;
}

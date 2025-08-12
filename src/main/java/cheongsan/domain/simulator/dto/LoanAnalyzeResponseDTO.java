package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanAnalyzeResponseDTO {
    private TotalComparisonResultDTO totalComparison;
    private InterestComparisonResultDTO interestComparison;
    private BigDecimal dsr;
}

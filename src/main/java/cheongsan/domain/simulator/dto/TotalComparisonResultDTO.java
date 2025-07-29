package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TotalComparisonResultDTO {
    private BigDecimal originalTotal;
    private BigDecimal newLoanTotal;
    private BigDecimal increaseRate;
}

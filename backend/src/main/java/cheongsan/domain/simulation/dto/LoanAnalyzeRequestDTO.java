package cheongsan.domain.simulation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanAnalyzeRequestDTO {
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private BigDecimal annualIncome;
    private long loanPeriod;
    private RepaymentType repaymentType;
}

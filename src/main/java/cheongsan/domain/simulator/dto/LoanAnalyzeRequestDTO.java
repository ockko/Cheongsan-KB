package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanAnalyzeRequestDTO {
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private BigDecimal annualIncome;
    private Long loanPeriod;
    private RepaymentType repaymentType;
}

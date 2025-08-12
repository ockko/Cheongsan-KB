package cheongsan.domain.deposit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class FinancialSummaryDTO {
    private BigDecimal monthlyIncome;
    private BigDecimal totalDebtPayment;
    private BigDecimal fixedExpense;
}

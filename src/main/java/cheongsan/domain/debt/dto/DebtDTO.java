package cheongsan.domain.debt.dto;

import cheongsan.common.util.LoanCalculator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebtDTO {

    private Long id;
    private Long userId;
    private Long organizationCode;
    private Long resAccount;
    private String debtName;
    private BigDecimal currentBalance;
    private BigDecimal originalAmount;
    private BigDecimal interestRate;
    private LocalDate loanStartDate;
    private LocalDate loanEndDate;
    private LocalDate nextPaymentDate;
    private Integer gracePeriodMonths;
    private String repaymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LoanCalculator.RepaymentMethod getRepaymentMethodEnum() {
        if ("원리금균등상환".equals(this.repaymentMethod)) {
            return LoanCalculator.RepaymentMethod.EQUAL_PRINCIPAL_INTEREST;
        } else if ("원금균등상환".equals(this.repaymentMethod)) {
            return LoanCalculator.RepaymentMethod.EQUAL_PRINCIPAL;
        } else if ("만기일시상환".equals(this.repaymentMethod)) {
            return LoanCalculator.RepaymentMethod.BULLET_REPAYMENT;
        }
        throw new IllegalArgumentException("Unknown repayment method: " + this.repaymentMethod);
    }
}

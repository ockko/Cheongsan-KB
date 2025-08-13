package cheongsan.domain.debt.dto;

import cheongsan.domain.simulator.dto.RepaymentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class DebtInfoResponseDTO {
    private Long debtId;
    private String debtName;
    private String organizationName;
    private BigDecimal originalAmount;
    private BigDecimal currentBalance;

    private BigDecimal repaymentRate; // originalAmount, currentBalance 로 계산한 상환율

    private BigDecimal interestRate;

    private RepaymentType repaymentType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate loanStartDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate loanEndDate;
    private LocalDate nextPaymentDate;
    private Long gracePeriodMonths;

}
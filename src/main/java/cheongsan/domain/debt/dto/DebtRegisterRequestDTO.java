package cheongsan.domain.debt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DebtRegisterRequestDTO {
    private String debtName;
    private String organizationName;
    private String resAccount;
    private BigDecimal originalAmount;
    private BigDecimal interestRate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate loanStartDate;
    private int totalMonths; // 총 상환 기간
    private BigDecimal currentBalance;
    private Long gracePeriodMonths;
    private String repaymentMethod;

}

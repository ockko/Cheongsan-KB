package cheongsan.domain.debt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class DebtDetailDTO {
    private String debtName;
    private String organizationName;
    private Long originalAmount;
    private Double interestRate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date loanStartDate;

    private Double currentBalance;
    private Integer gracePeriodMonths;
    private String repaymentMethod;
}

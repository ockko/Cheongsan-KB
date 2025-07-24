package cheongsan.domain.debt.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class DebtInfoDTO {
    private Long debtId;
    private String debtName;
    private String organizationName;
    private Long originalAmount;
    private Long currentBalance;

    private Double repaymentRate; // originalAmount, currentBalance 로 계산한 상환율

    private Double interestRate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date loanStartDate;


}
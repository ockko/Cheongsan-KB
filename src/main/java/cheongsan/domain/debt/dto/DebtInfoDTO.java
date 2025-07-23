package cheongsan.domain.debt.dto;

import lombok.Data;

@Data
public class DebtInfoDTO {
    private Long debtId;
    private String debtName;
    private String organizationName;
    private Long originalAmount;
    private Long currentBalance;

    private Double repaymentRate;


}

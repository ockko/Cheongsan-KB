package cheongsan.domain.debt.dto;

import cheongsan.domain.debt.entity.DebtAccount;
import cheongsan.domain.debt.entity.FinancialInstitution;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DebtDetailResponseDTO {
    private String debtName;
    private String organizationName;
    private Long originalAmount;
    private Double interestRate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate loanStartDate;

    private Double currentBalance;
    private Long gracePeriodMonths;
    private String repaymentMethod;

    public static DebtDetailResponseDTO fromEntity(DebtAccount da, FinancialInstitution fi) {
        return DebtDetailResponseDTO.builder()
                .debtName(da.getDebtName())
                .organizationName(fi.getOrganizationName())
                .originalAmount(da.getOriginalAmount().longValue())
                .interestRate(da.getInterestRate().doubleValue())
                .loanStartDate(da.getLoanStartDate())
                .currentBalance(da.getCurrentBalance().doubleValue())
                .gracePeriodMonths(da.getGracePeriodMonths())
                .repaymentMethod(da.getRepaymentMethod())
                .build();
    }
}

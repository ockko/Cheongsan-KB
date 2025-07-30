package cheongsan.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DebtUpdateResponseDTO {
    private Long debtId;
    private Long organizationCode;
    private String debtName;
    private BigDecimal currentBalance;
    private Long gracePeriodMonths;
    private String repaymentMethod;

}

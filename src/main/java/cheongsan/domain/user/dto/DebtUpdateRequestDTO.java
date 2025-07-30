package cheongsan.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DebtUpdateRequestDTO {
    private Long gracePeriodMonths;
    private String repaymentMethod;
    private LocalDate nextPaymentDate;
}

package cheongsan.domain.debt.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RepaymentInfoRequestDTO {
    private Long debtId;
    private LocalDate nextPaymentDate;
    private Long gracePeriod;
}

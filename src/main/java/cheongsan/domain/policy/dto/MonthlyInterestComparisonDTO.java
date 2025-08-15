package cheongsan.domain.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MonthlyInterestComparisonDTO {
    private LocalDate paymentDate;
    private BigDecimal existingInterest;
    private BigDecimal afterAddingInterest;
    private BigDecimal existingPayment;
    private BigDecimal afterAddingPayment;
}

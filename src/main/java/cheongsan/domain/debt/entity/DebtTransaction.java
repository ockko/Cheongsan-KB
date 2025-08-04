package cheongsan.domain.debt.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtTransaction {
    private Long id;
    private Long debtAccountId;
    private LocalDate transactionDate;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private BigDecimal remainingBalance;
    private LocalDateTime createdAt;
}

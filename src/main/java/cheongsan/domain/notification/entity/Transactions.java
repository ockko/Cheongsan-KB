package cheongsan.domain.notification.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transactions {
    private Long depositAccountId;
    private Long userId;
    private LocalDateTime transactionTime;
    BigDecimal afterBalance;
    BigDecimal amount;
    String type;
}

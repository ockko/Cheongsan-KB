package cheongsan.domain.spending.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    private Long id;
    private Long depositAccountId;
    private Long userId;
    private LocalDateTime transactionTime;
    private BigDecimal afterBalance;
    private BigDecimal amount;
    private String type;
    private String resAccountDesc1;
    private String resAccountDesc2;
    private String resAccountDesc3;
}

package cheongsan.domain.deposit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

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

    // JOIN용 필드들 - 실제 테이블에는 없지만 JOIN 결과로 가져오는 정보
    private String accountNumber;      // deposit_accounts.account_number
}

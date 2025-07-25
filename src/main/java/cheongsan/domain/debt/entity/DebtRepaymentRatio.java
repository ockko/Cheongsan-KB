package cheongsan.domain.debt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// 총 부채 상환율 계산에 사용
// debt_accounts 테이블의 일부 컬럼 매핑
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtRepaymentRatio {
    private Long userId;
    private BigDecimal currentBalance;    // 잔액
    private BigDecimal originalAmount;    // 원금
}

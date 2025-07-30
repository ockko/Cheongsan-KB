package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@ToString
public class TotalComparisonResultDTO {
    private BigDecimal originalTotal; // 기존 대출들만 고려한 총 상환액 (원금 + 이자 포함)
    private BigDecimal newLoanTotal; // 기존 대출 + 신규 대출 포함한 총 상환액
    private BigDecimal increaseRate; // 총 상환액 증가율 ((newLoanTotal - originalTotal) / originalTotal) × 100
}

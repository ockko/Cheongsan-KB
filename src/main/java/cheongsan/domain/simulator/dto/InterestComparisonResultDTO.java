package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class InterestComparisonResultDTO {
    private BigDecimal existingInterest;   // 기존 대출 총 이자
    private BigDecimal newLoanInterest;    // 신규 대출 포함 총 이자
}

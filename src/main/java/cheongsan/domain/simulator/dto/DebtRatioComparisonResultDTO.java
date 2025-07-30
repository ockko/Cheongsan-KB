package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class DebtRatioComparisonResultDTO {
    private BigDecimal existingDebtRatio; // 기존 부채비율(%)
    private BigDecimal newDebtRatio; // 신규 대출 포함 부채비율(%)
    private BigDecimal increaseAmount; // 부채비율 증가량 (단순 차이)
    private BigDecimal increaseRate; // 부채비율 증가율 (증가량 / 기존 비율 * 100)
}

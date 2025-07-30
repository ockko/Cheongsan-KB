package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class MonthlyPaymentDetailDTO {
    private long month; // 몇 번째 상환월인지 (1 ~ 상환 개월 수)
    private BigDecimal principal; // 해당 월에 상환한 원금
    private BigDecimal interest; //  해당 월에 발생한 이자
    private BigDecimal totalPayment; // 해당 월의 총 납입액 (원금 + 이자)
}

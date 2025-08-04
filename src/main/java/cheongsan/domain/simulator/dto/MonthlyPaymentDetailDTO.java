package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MonthlyPaymentDetailDTO {
    private long month; // 몇 번째 상환월인지 (1 ~ 상환 개월 수)
    private BigDecimal principal; // 해당 월에 상환한 원금
    private BigDecimal interest; //  해당 월에 발생한 이자
    private BigDecimal prepayment; // 중도상환 금액
    private BigDecimal prepaymentFee; // 중도상환 수수료
    private BigDecimal totalPayment; // 해당 월의 총 납입액 (원금 + 이자)
    private LocalDate paymentDate; // 실제 납입 날
    private BigDecimal remainingPrincipal; // 이달 납부 후 남은 원금
}

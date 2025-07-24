package cheongsan.domain.simulator.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentRequestDto {
    private List<LoanDto> loans; // 사용자의 대출 리스트
    private BigDecimal monthlyPayment; // 기존 월 상환액
    private BigDecimal extraPaymentAmount; // 추가 상환액 (nullable 또는 0)
    private BigDecimal monthlyAvailableAmount;
}

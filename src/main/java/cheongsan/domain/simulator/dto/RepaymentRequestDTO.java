package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentRequestDTO {
    private List<LoanDTO> loans; // 사용자의 대출 리스트
    private BigDecimal monthlyPayment; // 기존 월 상환액
    private BigDecimal extraPaymentAmount; // 추가 상환액 (nullable 또는 0)
    private BigDecimal monthlyAvailableAmount;
}

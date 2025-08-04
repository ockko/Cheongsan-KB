package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResultDTO {
    private BigDecimal totalPayment;
    private BigDecimal totalInterest;
    private BigDecimal totalPrepaymentFee;
    private List<MonthlyPaymentDetailDTO> payments;
    private LocalDate debtFreeDate;
    private long actualMonthCount;
}

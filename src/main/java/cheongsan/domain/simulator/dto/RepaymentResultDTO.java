package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class RepaymentResultDTO {
    private BigDecimal totalPayment;
    private List<MonthlyPaymentDetailDTO> payments;
}

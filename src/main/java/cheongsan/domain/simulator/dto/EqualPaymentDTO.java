package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class EqualPaymentDTO {
    private BigDecimal totalPayment;
    private BigDecimal monthlyPayment;
}

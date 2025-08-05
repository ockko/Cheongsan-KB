package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class RepaymentResultDTO {
    private BigDecimal existingRepaymentAmount;
    private BigDecimal additionalRepaymentAmount;
    private BigDecimal totalRepaymentAmount;
    private List<RepaymentResponseDTO> repayments;
}

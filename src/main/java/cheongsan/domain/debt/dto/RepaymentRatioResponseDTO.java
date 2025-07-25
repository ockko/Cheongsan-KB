package cheongsan.domain.debt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class RepaymentRatioResponseDTO {
    private BigDecimal totalOriginalAmount;
    private BigDecimal totalRepaidAmount;
    private BigDecimal repaymentRatio;
}
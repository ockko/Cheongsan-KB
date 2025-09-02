package cheongsan.domain.debt.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RepaymentRatioResponseDTO {
    private BigDecimal totalOriginalAmount;
    private BigDecimal totalRepaidAmount;
    private BigDecimal repaymentRatio;
}
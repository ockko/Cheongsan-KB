package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisDTO {
    private BigDecimal totalPrincipal;
    private BigDecimal totalInterest;
    private BigDecimal totalRepayment;
    private BigDecimal debtServiceRatio;
    private List<MonthlyPaymentDetailDTO> monthlyBreakdown = new ArrayList<>();
}

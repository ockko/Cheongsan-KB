package cheongsan.domain.simulation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentResponseDTO {
    private StrategyType strategyType; // 어떤 전략
    private long totalMonths; // 총 상환 기간(개월)
    private BigDecimal interestSaved; // 절약 이자
    private BigDecimal totalPayment; // 전략 적용 후 총 상환액
    private BigDecimal originalPayment; // 원래 총 상환액
    private BigDecimal totalPrepaymentFee; // 중도상환수수료 총합
    private List<String> sortedLoanNames;
    private Map<String, List<MonthlyPaymentDetailDTO>> repaymentHistory;
    private Map<String, LocalDate> debtFreeDates; // 만기일시
}

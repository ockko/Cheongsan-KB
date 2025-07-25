package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentResultDTO {

    private StrategyType strategyType; // 어떤 전략
    private LocalDate debtFreeDate; // 최종 졸업일
    private int totalMonths; // 총 상환 기간(개월)
    private BigDecimal interestSaved; // 절약 이자
    private List<String> sortedLoanNames;
}

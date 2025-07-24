package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RepaymentResultDto {

    private StrategyType strategyType; // 어떤 전략
    private LocalDate debtFreeDate; // 최종 졸업일
    private int totalMonths; // 총 상환 기간(개월)
    private BigDecimal interestSaved; // 절약 이자
    private List<String> sortedLoanNames;
}

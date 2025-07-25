package cheongsan.domain.simulator.service.strategy;

import cheongsan.domain.simulator.dto.LoanDTO;
import cheongsan.domain.simulator.dto.RepaymentRequestDTO;
import cheongsan.domain.simulator.dto.RepaymentResultDTO;
import cheongsan.domain.simulator.dto.StrategyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OldestFirstStrategy implements RepaymentStrategy {

    @Override
    public RepaymentResultDTO simulate(RepaymentRequestDTO request) {
        List<LoanDTO> loans = new ArrayList<>(request.getLoans());
        loans.sort(Comparator.comparing(LoanDTO::getStartDate)); // 시작일 기준 오름차순 정렬

        List<String> sortedLoanNames = loans.stream()
                .map(LoanDTO::getLoanName)
                .collect(Collectors.toList());

        BigDecimal monthlyAvailable = request.getMonthlyAvailableAmount();
        LocalDate currentMonth = LocalDate.now();
        BigDecimal totalInterestPaid = BigDecimal.ZERO;

        Map<Long, BigDecimal> remainingBalances = loans.stream()
                .collect(Collectors.toMap(LoanDTO::getId, LoanDTO::getPrincipal));

        int monthsElapsed = 0;

        while (remainingBalances.values().stream().anyMatch(b -> b.compareTo(BigDecimal.ZERO) > 0)) {
            BigDecimal available = monthlyAvailable;

            for (LoanDTO loan : loans) {
                Long id = loan.getId();
                BigDecimal principal = remainingBalances.get(id);
                if (principal.compareTo(BigDecimal.ZERO) <= 0) continue;

                // 간단 이자 계산
                BigDecimal monthlyRate = BigDecimal.valueOf(loan.getInterestRate())
                        .divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);
                BigDecimal interestPaid = principal.multiply(monthlyRate);

                totalInterestPaid = totalInterestPaid.add(interestPaid);

                BigDecimal payment = principal.min(available);
                BigDecimal newPrincipal = principal.subtract(payment);

                remainingBalances.put(id, newPrincipal.max(BigDecimal.ZERO));
                available = available.subtract(payment);

                if (available.compareTo(BigDecimal.ZERO) <= 0) break;
            }

            currentMonth = currentMonth.plusMonths(1);
            monthsElapsed++;
        }

        return RepaymentResultDTO.builder()
                .strategyType(StrategyType.OLDEST_FIRST)
                .debtFreeDate(LocalDate.now().plusMonths(monthsElapsed))
                .totalMonths(monthsElapsed)
                .interestSaved(BigDecimal.ZERO) // 비교 전략이 없으므로 0
                .sortedLoanNames(sortedLoanNames)
                .build();
    }

    @Override
    public StrategyType getStrategyType() {
        return StrategyType.OLDEST_FIRST;
    }
}

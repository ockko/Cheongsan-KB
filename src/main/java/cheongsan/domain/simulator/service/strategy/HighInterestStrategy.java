package cheongsan.domain.simulator.service.strategy;

import cheongsan.domain.simulator.dto.LoanDto;
import cheongsan.domain.simulator.dto.RepaymentRequestDto;
import cheongsan.domain.simulator.dto.RepaymentResultDto;
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
public class HighInterestStrategy implements RepaymentStrategy {

    @Override
    public RepaymentResultDto simulate(RepaymentRequestDto request) {
        List<LoanDto> loans = new ArrayList<>(request.getLoans());

        // 이자율 내림차순 정렬
        loans.sort(Comparator.comparing(LoanDto::getInterestRate).reversed());

        // 정렬된 대출명 리스트 추출
        List<String> sortedLoanNames = loans.stream()
                .map(LoanDto::getLoanName)
                .collect(Collectors.toList());

        BigDecimal monthlyAvailable = request.getMonthlyAvailableAmount();
        LocalDate currentMonth = LocalDate.now();
        BigDecimal totalInterestPaid = BigDecimal.ZERO;

        Map<Long, BigDecimal> remainingBalances = loans.stream()
                .collect(Collectors.toMap(LoanDto::getId, LoanDto::getPrincipal));

        int monthsElapsed = 0;

        while (remainingBalances.values().stream().anyMatch(b -> b.compareTo(BigDecimal.ZERO) > 0)) {
            BigDecimal available = monthlyAvailable;

            for (LoanDto loan : loans) {
                Long id = loan.getId();
                BigDecimal principal = remainingBalances.get(id);
                if (principal.compareTo(BigDecimal.ZERO) <= 0) continue;

                BigDecimal monthlyRate = BigDecimal.valueOf(loan.getInterestRate())
                        .divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);
                BigDecimal interestPaid = principal.multiply(monthlyRate);

                totalInterestPaid = totalInterestPaid.add(interestPaid);

                BigDecimal payment = available.min(principal);
                BigDecimal newPrincipal = principal.subtract(payment);

                remainingBalances.put(id, newPrincipal.max(BigDecimal.ZERO));
                available = available.subtract(payment);

                if (available.compareTo(BigDecimal.ZERO) <= 0) break;
            }

            currentMonth = currentMonth.plusMonths(1);
            monthsElapsed++;
        }

        return RepaymentResultDto.builder()
                .strategyType(StrategyType.HIGH_INTEREST_FIRST)
                .debtFreeDate(LocalDate.now().plusMonths(monthsElapsed))
                .totalMonths(monthsElapsed)
                .interestSaved(BigDecimal.ZERO) // 기준 시나리오 없으므로 0
                .sortedLoanNames(sortedLoanNames)
                .build();
    }

    @Override
    public StrategyType getStrategyType() {
        return StrategyType.HIGH_INTEREST_FIRST;
    }
}


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
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TcsStrategy implements RepaymentStrategy {

    private final List<String> priorityOrder = Arrays.asList("금융2", "기타", "금융1");

    private int getInstitutionPriority(String type) {
        return priorityOrder.indexOf(type);
    }

    @Override
    public RepaymentResultDTO simulate(RepaymentRequestDTO request) {
        List<LoanDTO> loans = new ArrayList<>(request.getLoans());

        // 우선순위 정렬
        loans.sort(
                Comparator.comparing((LoanDTO l) -> getInstitutionPriority(l.getInstitutionType()))
                        .thenComparing(LoanDTO::getInterestRate, Comparator.reverseOrder())
                        .thenComparing(LoanDTO::getPrincipal)
                        .thenComparing(LoanDTO::getStartDate)
        );

        List<String> sortedLoanNames = loans.stream()
                .map(LoanDTO::getLoanName)
                .collect(Collectors.toList());

        Map<Long, BigDecimal> remainingBalances = loans.stream()
                .collect(Collectors.toMap(LoanDTO::getId, LoanDTO::getPrincipal));

        BigDecimal totalInterestPaid = BigDecimal.ZERO;
        LocalDate currentMonth = LocalDate.now();
        int monthsElapsed = 0;

        while (remainingBalances.values().stream().anyMatch(b -> b.compareTo(BigDecimal.ZERO) > 0)) {
            BigDecimal extra = Optional.ofNullable(request.getExtraPaymentAmount()).orElse(BigDecimal.ZERO);

            // 기본 monthlyPayment만큼 각 대출에서 차감
            for (LoanDTO loan : loans) {
                Long id = loan.getId();
                BigDecimal principal = remainingBalances.get(id);
                if (principal.compareTo(BigDecimal.ZERO) <= 0) continue;

                BigDecimal monthlyPayment = loan.getMonthlyPayment();
                BigDecimal payment = monthlyPayment.min(principal);
                BigDecimal monthlyRate = loan.getInterestRate()
                        .divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);

                BigDecimal interestPaid = principal.multiply(monthlyRate);
                totalInterestPaid = totalInterestPaid.add(interestPaid);

                remainingBalances.put(id, principal.subtract(payment));
            }

            // 추가 상환액이 있다면 가장 우선 대출 1건에 몰빵
            if (extra.compareTo(BigDecimal.ZERO) > 0) {
                for (LoanDTO loan : loans) {
                    Long id = loan.getId();
                    BigDecimal principal = remainingBalances.get(id);
                    if (principal.compareTo(BigDecimal.ZERO) <= 0) continue;

                    BigDecimal extraPayment = extra.min(principal);
                    remainingBalances.put(id, principal.subtract(extraPayment));
                    break; // 한 건에만 몰빵
                }
            }

            currentMonth = currentMonth.plusMonths(1);
            monthsElapsed++;
        }

        return RepaymentResultDTO.builder()
                .strategyType(StrategyType.TCS_RECOMMEND)
                .debtFreeDate(LocalDate.now().plusMonths(monthsElapsed))
                .totalMonths(monthsElapsed)
                .interestSaved(BigDecimal.ZERO)
                .sortedLoanNames(sortedLoanNames)
                .build();
    }

    @Override
    public StrategyType getStrategyType() {
        return StrategyType.TCS_RECOMMEND;
    }
}

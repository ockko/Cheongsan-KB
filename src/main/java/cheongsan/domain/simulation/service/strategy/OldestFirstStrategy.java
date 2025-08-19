package cheongsan.domain.simulation.service.strategy;

import cheongsan.domain.simulation.dto.*;
import cheongsan.domain.simulation.service.LoanRepaymentCalculatorFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OldestFirstStrategy implements RepaymentStrategy {

    private final LoanRepaymentCalculatorFacade calculatorFacade;

    @Override
    public RepaymentResponseDTO simulate(RepaymentRequestDTO request, List<LoanDTO> originalLoans) {
        // 만기일 기준 오름차순 정렬 (가까운 만기부터)
        List<LoanDTO> loans = originalLoans.stream()
                .sorted(Comparator.comparing(LoanDTO::getEndDate))
                .collect(Collectors.toList());

        List<String> sortedLoanNames = loans.stream()
                .map(LoanDTO::getLoanName)
                .collect(Collectors.toList());

        BigDecimal monthlyAvailable = request.getMonthlyAvailableAmount();

        if (monthlyAvailable.compareTo(BigDecimal.ZERO) == 0) {
            return simulateWithoutPrepayment(originalLoans, sortedLoanNames);
        }
        BigDecimal totalPayment = BigDecimal.ZERO;
        BigDecimal originalTotalPayment = BigDecimal.ZERO;
        BigDecimal penaltyLoss = BigDecimal.ZERO;

        List<PaymentResultDTO> paymentResults = originalLoans.stream()
                .map(loan -> calculatorFacade.calculateWithFixedPrepayment(loan, BigDecimal.ZERO)) // ← 중도상환 0원으로 계산
                .collect(Collectors.toList());

        originalTotalPayment = paymentResults.stream()
                .map(PaymentResultDTO::getTotalPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<SimulatedLoanDTO> activeLoans = loans.stream()
                .map(loan -> {
                    LocalDate startDate = calculateNextPaymentDate(loan.getPaymentDate().getDayOfMonth(), LocalDate.now());
                    return SimulatedLoanDTO.from(loan, startDate);
                })
                .collect(Collectors.toList());

        int monthCount = 0;
        int prepayIndex = 0;


        while (activeLoans.stream().anyMatch(loan -> !loan.isFinished())) {
            monthCount++;
            BigDecimal available = monthlyAvailable;

            for (int i = 0; i < activeLoans.size(); i++) {
                SimulatedLoanDTO loan = activeLoans.get(i);
                if (loan.isFinished()) continue; // 이미 끝난 대출은 skip

                boolean isPrepayTarget = (i == prepayIndex);

                BigDecimal extraRepayment = BigDecimal.ZERO;
                Scenario scenario = Scenario.NO_PREPAYMENT;

                if (isPrepayTarget && available.compareTo(BigDecimal.ZERO) > 0) {
                    extraRepayment = available.min(loan.getRemainingPrincipal());
                    scenario = Scenario.WITH_PREPAYMENT;
                }

                LoanDTO loanForThisMonth = LoanDTO.builder()
                        .id(loan.getBaseLoan().getId())
                        .loanName(loan.getBaseLoan().getLoanName())
                        .principal(loan.getRemainingPrincipal())  // 잔여 원금 반영
                        .interestRate(loan.getBaseLoan().getInterestRate())
                        .startDate(loan.getBaseLoan().getStartDate())
                        .endDate(loan.getBaseLoan().getEndDate())
                        .institutionType(loan.getBaseLoan().getInstitutionType())
                        .repaymentType(loan.getBaseLoan().getRepaymentType())
                        .prepaymentFeeRate(loan.getBaseLoan().getPrepaymentFeeRate())
                        .paymentDate(loan.getBaseLoan().getPaymentDate())
                        .build();
                MonthlyPaymentDetailDTO payment = calculatorFacade.simulateMonthly(
                        loanForThisMonth,
                        loan.getCurrentDate(),
                        extraRepayment,
                        scenario
                );

                loan.applyMonthlyPayment(payment);
                loan.incrementMonth();

                if (isPrepayTarget) {
                    available = available.subtract(extraRepayment);
                    penaltyLoss = penaltyLoss.add(payment.getPrepaymentFee());
                }

                totalPayment = totalPayment.add(payment.getTotalPayment());

                if (loan.isFinished()) {
                    loan.setDebtFreeDate(payment.getPaymentDate()); // 최종 상환일 저장
                    if (isPrepayTarget && prepayIndex + 1 < activeLoans.size()) {
                        prepayIndex++;
                    }
                }
            }
        }


        Map<String, List<MonthlyPaymentDetailDTO>> repaymentHistory = activeLoans.stream()
                .collect(Collectors.toMap(
                        l -> l.getBaseLoan().getLoanName(),
                        SimulatedLoanDTO::getPaymentHistory
                ));

        Map<String, LocalDate> debtFreeDates = activeLoans.stream()
                .collect(Collectors.toMap(
                        loan -> loan.getBaseLoan().getLoanName(),
                        SimulatedLoanDTO::getDebtFreeDate
                ));
        return RepaymentResponseDTO.builder()
                .strategyType(StrategyType.OLDEST_FIRST)
                .debtFreeDates(debtFreeDates)
                .totalMonths(monthCount)
                .totalPayment(totalPayment)
                .originalPayment(originalTotalPayment)
                .interestSaved(originalTotalPayment.subtract(totalPayment))
                .totalPrepaymentFee(penaltyLoss)
                .sortedLoanNames(sortedLoanNames)
                .repaymentHistory(repaymentHistory)
                .build();
    }

    private LocalDate calculateNextPaymentDate(int paymentDay, LocalDate today) {
        // 이번 달 상환일
        LocalDate scheduledThisMonth = today.withDayOfMonth(Math.min(paymentDay, today.lengthOfMonth()));

        // 오늘이 상환일을 지났으면 다음 달로
        if (!today.isBefore(scheduledThisMonth)) {
            LocalDate nextMonth = today.plusMonths(1);
            return nextMonth.withDayOfMonth(Math.min(paymentDay, nextMonth.lengthOfMonth()));
        }
        return scheduledThisMonth;
    }

    @Override
    public StrategyType getStrategyType() {
        return StrategyType.OLDEST_FIRST;
    }

    private RepaymentResponseDTO simulateWithoutPrepayment(List<LoanDTO> originalLoans, List<String> sortedLoanNames) {
        List<PaymentResultDTO> paymentResults = originalLoans.stream()
                .map(calculatorFacade::calculateWithoutPrepayment)
                .collect(Collectors.toList());

        BigDecimal totalPayment = paymentResults.stream()
                .map(PaymentResultDTO::getTotalPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 각 대출의 완납일 계산
        Map<String, LocalDate> debtFreeDates = new HashMap<>();
        Map<String, List<MonthlyPaymentDetailDTO>> repaymentHistory = new HashMap<>();

        int maxMonths = 0;
        for (int i = 0; i < originalLoans.size(); i++) {
            LoanDTO loan = originalLoans.get(i);
            PaymentResultDTO result = paymentResults.get(i);

            // 마지막 납부일을 완납일로 설정
            if (!result.getPayments().isEmpty()) {
                MonthlyPaymentDetailDTO lastPayment = result.getPayments().get(result.getPayments().size() - 1);
                debtFreeDates.put(loan.getLoanName(), lastPayment.getPaymentDate());
                maxMonths = Math.max(maxMonths, result.getPayments().size());
            }

            repaymentHistory.put(loan.getLoanName(), result.getPayments());
        }

        return RepaymentResponseDTO.builder()
                .strategyType(StrategyType.OLDEST_FIRST)
                .debtFreeDates(debtFreeDates)
                .totalMonths(maxMonths)
                .totalPayment(totalPayment)
                .originalPayment(totalPayment)  // 중도상환 없으므로 동일
                .interestSaved(BigDecimal.ZERO)  // 절약 없음
                .totalPrepaymentFee(BigDecimal.ZERO)  // 중도상환 수수료 없음
                .sortedLoanNames(sortedLoanNames)
                .repaymentHistory(repaymentHistory)
                .build();
    }
}

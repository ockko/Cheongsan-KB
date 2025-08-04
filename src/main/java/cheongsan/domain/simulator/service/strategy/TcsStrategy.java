package cheongsan.domain.simulator.service.strategy;

import cheongsan.domain.simulator.dto.*;
import cheongsan.domain.simulator.service.LoanRepaymentCalculatorFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TcsStrategy implements RepaymentStrategy {

    private final LoanRepaymentCalculatorFacade calculatorFacade;

    @Override
    public RepaymentResponseDTO simulate(RepaymentRequestDTO request, List<LoanDTO> originalLoans) {
        List<LoanDTO> sortedLoans = originalLoans.stream()
                .sorted(
                        Comparator.comparing((LoanDTO loan) -> !isPrivateLoan(loan))  // 사금융 우선
                                .thenComparing(LoanDTO::getInterestRate, Comparator.reverseOrder()) // 고금리 우선
                )
                .collect(Collectors.toList());


        List<String> sortedLoanNames = sortedLoans.stream()
                .map(LoanDTO::getLoanName)
                .collect(Collectors.toList());

        BigDecimal monthlyAvailable = request.getMonthlyAvailableAmount();
        BigDecimal totalPayment = BigDecimal.ZERO;
        BigDecimal originalTotalPayment = BigDecimal.ZERO;
        BigDecimal penaltyLoss = BigDecimal.ZERO;

        List<PaymentResultDTO> paymentResults = sortedLoans.stream()
                .map(calculatorFacade::calculateWithoutPrepayment)
                .collect(Collectors.toList());

        originalTotalPayment = paymentResults.stream()
                .map(PaymentResultDTO::getTotalPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<SimulatedLoanDTO> activeLoans = sortedLoans.stream()
                .map(loan -> {
                    LocalDate startDate = calculateNextPaymentDate(loan.getPaymentDate().getDayOfMonth(), LocalDate.now());
                    return SimulatedLoanDTO.from(loan, startDate);
                })
                .collect(Collectors.toList());


        int monthCount = 0;
        int prepayIndex = 0;

        while (activeLoans.stream().anyMatch(l -> !l.isFinished())) {
            monthCount++;
            BigDecimal available = monthlyAvailable;

            for (int i = 0; i < activeLoans.size(); i++) {
                SimulatedLoanDTO loan = activeLoans.get(i);

                if (loan.isFinished()) continue;

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
                    loan.setDebtFreeDate(payment.getPaymentDate());
                    if (isPrepayTarget && prepayIndex + 1 < activeLoans.size()) {
                        prepayIndex++;
                    }
                }
            }
        }

        Map<String, LocalDate> debtFreeDates = activeLoans.stream()
                .collect(Collectors.toMap(
                        loan -> loan.getBaseLoan().getLoanName(),
                        SimulatedLoanDTO::getDebtFreeDate
                ));


        Map<String, List<MonthlyPaymentDetailDTO>> repaymentHistory = activeLoans.stream()
                .collect(Collectors.toMap(
                        l -> l.getBaseLoan().getLoanName(),
                        SimulatedLoanDTO::getPaymentHistory
                ));

        return RepaymentResponseDTO.builder()
                .strategyType(StrategyType.TCS_RECOMMEND)
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

    private boolean isPrivateLoan(LoanDTO loan) {
        String type = loan.getInstitutionType();
        return List.of("사금융", "카드론", "대부업", "P2P").stream()
                .anyMatch(type::contains);  // 부분 문자열 검사
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
        return StrategyType.TCS_RECOMMEND;
    }
}

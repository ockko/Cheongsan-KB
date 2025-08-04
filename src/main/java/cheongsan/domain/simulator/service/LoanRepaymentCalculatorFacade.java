package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.LoanDTO;
import cheongsan.domain.simulator.dto.MonthlyPaymentDetailDTO;
import cheongsan.domain.simulator.dto.PaymentResultDTO;
import cheongsan.domain.simulator.dto.Scenario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class LoanRepaymentCalculatorFacade {

    private final LoanRepaymentCalculator repaymentCalculator;
    private final MonthlyRepaymentCalculator monthlyRepaymentCalculator;

    public PaymentResultDTO calculateByType(
            Scenario scenario,
            LoanDTO loan,
            BigDecimal monthlyPrepayment // scenario가 prepayment일 경우만 사용
    ) {
        switch (scenario) {
            case NO_PREPAYMENT:
                return calculateWithoutPrepayment(loan);
            case WITH_PREPAYMENT:
                return calculateWithFixedPrepayment(loan, monthlyPrepayment);
            default:
                throw new IllegalArgumentException("알 수 없는 시나리오: " + scenario);
        }
    }

    public PaymentResultDTO calculateWithoutPrepayment(LoanDTO loan) {
        switch (loan.getRepaymentType()) {
            case EQUAL_PAYMENT:
                return repaymentCalculator.calculateEqualPayment(loan);
            case EQUAL_PRINCIPAL:
                return repaymentCalculator.calculateEqualPrincipal(loan);
            case LUMP_SUM:
                return repaymentCalculator.calculateLumpSumRepayment(loan);
            default:
                throw new IllegalArgumentException("알 수 없는 상환 유형: " + loan.getRepaymentType());
        }
    }

    public PaymentResultDTO calculateWithFixedPrepayment(LoanDTO loan, BigDecimal monthlyPrepayment) {
        switch (loan.getRepaymentType()) {
            case EQUAL_PAYMENT:
                return repaymentCalculator.calculateEqualPaymentWithFixedPrepayment(monthlyPrepayment, loan);
            case EQUAL_PRINCIPAL:
                return repaymentCalculator.calculateEqualPrincipalWithFixedPrepayment(monthlyPrepayment, loan);
            case LUMP_SUM:
                return repaymentCalculator.calculateLumpSumRepaymentWithPrepayment(monthlyPrepayment, loan);
            default:
                throw new IllegalArgumentException("알 수 없는 상환 유형: " + loan.getRepaymentType());
        }
    }


    public MonthlyPaymentDetailDTO simulateMonthly(
            LoanDTO loan,
            LocalDate date,
            BigDecimal extraPayment,
            Scenario scenario
    ) {
        switch (loan.getRepaymentType()) {
            case EQUAL_PAYMENT:
                if (scenario == Scenario.WITH_PREPAYMENT) {
                    return monthlyRepaymentCalculator.equalPaymentWithPrePayment(extraPayment, loan, date);
                } else {
                    return monthlyRepaymentCalculator.simulateMonthlyEqualPayment(loan, date, extraPayment, scenario);
                }

            case EQUAL_PRINCIPAL:
                if (scenario == Scenario.WITH_PREPAYMENT) {
                    return monthlyRepaymentCalculator.equalPrincipalWithPrePayment(extraPayment, loan, date);
                } else {
                    return monthlyRepaymentCalculator.simulateMonthlyEqualPrincipal(loan, date, extraPayment, scenario);
                }

            case LUMP_SUM:
                if (scenario == Scenario.WITH_PREPAYMENT) {
                    return monthlyRepaymentCalculator.calculateLumpSumRepaymentWithPrepaymentMonthly(extraPayment, loan, date);
                } else {
                    return monthlyRepaymentCalculator.calculateLumpSumRepaymentMonthly(loan, date);
                }

            default:
                throw new IllegalArgumentException("알 수 없는 상환 방식: " + loan.getRepaymentType());
        }
    }


}

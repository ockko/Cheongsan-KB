package cheongsan.common.util;

import cheongsan.domain.simulator.dto.LoanDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

@Component
public class DsrCalculator {

    // 기존 대출 포함 DSR 계산
    public static BigDecimal calculateDsr(
            List<LoanDTO> existingLoans,
            BigDecimal newMonthlyRepayment,
            BigDecimal annualIncome,
            LoanCalculator calculator
    ) {
        BigDecimal existingTotalMonthly = existingLoans.stream()
                .map(loan -> {
                    LoanCalculator.RepaymentMethod method = RepaymentTypeMapper.toMethod(loan.getRepaymentType());
                    BigDecimal monthly = calculator.calculateMonthlyPayment(
                            method,
                            loan.getPrincipal(),
                            loan.getPrincipal(),
                            loan.getInterestRate(),
                            loan.getStartDate(),
                            loan.getEndDate()
                    );

                    System.out.println("=== DSR 계산 대상 대출 ===");
                    System.out.println("▶ 상환 방식: " + method);
                    System.out.println("▶ 원금: " + loan.getPrincipal());
                    System.out.println("▶ 이자율: " + loan.getInterestRate());
                    System.out.println("▶ 시작일: " + loan.getStartDate());
                    System.out.println("▶ 종료일: " + loan.getEndDate());
                    System.out.println("▶ 월 상환액: " + monthly);

                    return monthly;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal totalMonthlyRepayment = existingTotalMonthly.add(newMonthlyRepayment);
        BigDecimal yearlyRepayment = totalMonthlyRepayment.multiply(BigDecimal.valueOf(12));

        return yearlyRepayment.divide(annualIncome, 5, RoundingMode.HALF_UP);
    }

    // 기존 대출 없을 때 DSR 계산
    public static BigDecimal calculateDsr(
            BigDecimal monthlyRepayment,
            BigDecimal annualIncome,
            LoanCalculator calculator
    ) {
        return calculateDsr(Collections.emptyList(), monthlyRepayment, annualIncome, calculator);
    }
}

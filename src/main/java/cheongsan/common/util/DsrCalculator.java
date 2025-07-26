package cheongsan.common.util;

import cheongsan.domain.simulator.dto.LoanDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

public class DsrCalculator {

    // 기존 대출 포함 DSR 계산
    public static BigDecimal calculateDsr(
            List<LoanDTO> existingLoans,
            BigDecimal newMonthlyRepayment,
            BigDecimal annualIncome
    ) {
        BigDecimal existingTotalMonthly = existingLoans.stream()
                .map(LoanDTO::getMonthlyPayment)
                .filter(payment -> payment != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalMonthlyRepayment = existingTotalMonthly.add(newMonthlyRepayment);
        BigDecimal yearlyRepayment = totalMonthlyRepayment.multiply(BigDecimal.valueOf(12));

        return yearlyRepayment.divide(annualIncome, 5, RoundingMode.HALF_UP);
    }

    // 기존 대출 없을 때 DSR 계산
    public static BigDecimal calculateDsr(
            BigDecimal monthlyRepayment,
            BigDecimal annualIncome
    ) {
        return calculateDsr(Collections.emptyList(), monthlyRepayment, annualIncome);
    }
}

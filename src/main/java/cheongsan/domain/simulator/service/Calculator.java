package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.EqualPaymentDTO;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Calculator {

    private static final MathContext MATH_CONTEXT = new MathContext(15, RoundingMode.HALF_UP);

    public EqualPaymentDTO calculateEqualPayment(BigDecimal interestRate, BigDecimal months, BigDecimal loanAmount) {
        if (interestRate.equals(BigDecimal.ZERO)) {
            return new EqualPaymentDTO(loanAmount.negate(), loanAmount.negate().divide(months, MATH_CONTEXT));
        }
        if (months.signum() < 0) {
            throw new ArithmeticException("months must not be negative");
        }
        BigDecimal one = BigDecimal.ONE;
        BigDecimal powResult = one.add(interestRate).pow(months.negate().intValueExact(), MATH_CONTEXT);
        BigDecimal denominator = one.subtract(powResult, MATH_CONTEXT);
        BigDecimal numerator = interestRate.multiply(loanAmount, MATH_CONTEXT);
        BigDecimal monthlyPayment = numerator.divide(denominator, MATH_CONTEXT);
        return new EqualPaymentDTO(monthlyPayment.multiply(months), monthlyPayment);
    }
}

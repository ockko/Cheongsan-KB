package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.EqualPaymentDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculateTest {

    private static final MathContext MATH_CONTEXT = new MathContext(15, RoundingMode.HALF_UP);
    private final Calculator calculator = new Calculator();

    @Test
    @DisplayName("원리금균등상환 계산 - 정상 입력")
    void calculateEqualPayment_validInput() {

        // given
        BigDecimal interestRate = new BigDecimal("0.005"); // 0.5% per month
        BigDecimal months = new BigDecimal("24");
        BigDecimal loanAmount = new BigDecimal("10000000");

        // when
        EqualPaymentDTO result = calculator.calculateEqualPayment(interestRate, months, loanAmount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMonthlyPayment()).isNotNull();
        assertThat(result.getTotalPayment()).isNotNull();

        // 총 납입금액은 월납입금 * 개월 수
        BigDecimal expectedTotal = result.getMonthlyPayment().multiply(months).setScale(0, RoundingMode.DOWN);
        assertThat(result.getTotalPayment().setScale(0, RoundingMode.DOWN))
                .isEqualByComparingTo(expectedTotal);
    }

    @Test
    @DisplayName("이율 0일 경우 - 단순 분할")
    void calculateEqualPayment_zeroInterest() {

        // given
        BigDecimal interestRate = BigDecimal.ZERO;
        BigDecimal months = new BigDecimal("12");
        BigDecimal loanAmount = new BigDecimal("1200000");

        // when
        EqualPaymentDTO result = calculator.calculateEqualPayment(interestRate, months, loanAmount);

        // then
        BigDecimal expectedMonthly = loanAmount.negate().divide(months, MATH_CONTEXT);
        BigDecimal expectedTotal = expectedMonthly.multiply(months);

        assertThat(result.getMonthlyPayment()).isEqualByComparingTo(expectedMonthly);
        assertThat(result.getTotalPayment()).isEqualByComparingTo(expectedTotal);
    }

    @Test
    @DisplayName("음수 개월 수 입력시 예외 발생")
    void calculateEqualPayment_negativeMonths() {
        // given
        BigDecimal interestRate = new BigDecimal("0.01");
        BigDecimal months = new BigDecimal("-10");
        BigDecimal loanAmount = new BigDecimal("1000000");

        // when & then
        assertThrows(ArithmeticException.class, () -> {
            calculator.calculateEqualPayment(interestRate, months, loanAmount);
        });
    }
}
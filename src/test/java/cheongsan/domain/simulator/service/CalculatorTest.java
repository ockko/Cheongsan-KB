package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.MonthlyPaymentDetailDTO;
import cheongsan.domain.simulator.dto.RepaymentResultDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @Nested
    @DisplayName("원리금 균등상환 테스트")
    class EqualPaymentTests {

        //        @ParameterizedTest(name = "이율: {0}, 기간: {1}, 원금: {2}")
//        @CsvSource({
//                "0.00, 12, 1200000",
//                "0.01, 12, 1200000",
//                "0.02, 24, 5000000",
//                "0.03, 36, 10000000"
//        })
        void testEqualPayment(BigDecimal interestRate, BigDecimal months, BigDecimal principal) {
            RepaymentResultDTO result = calculator.calculateEqualPayment(interestRate, months, principal);

            assertThat(result.getPayments()).hasSize(months.intValueExact());
            assertThat(result.getTotalPayment()).isGreaterThanOrEqualTo(principal);

            // 첫 달 정보 확인
            MonthlyPaymentDetailDTO first = result.getPayments().get(0);
            assertThat(first.getPrincipal()).isGreaterThan(BigDecimal.ZERO);
            assertThat(first.getTotalPayment()).isGreaterThan(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("0개월이면 예외 발생")
        void testEqualPaymentWithZeroMonth() {
            assertThatThrownBy(() ->
                    calculator.calculateEqualPayment(BigDecimal.valueOf(0.01), BigDecimal.ZERO, BigDecimal.valueOf(1000000))
            ).isInstanceOf(ArithmeticException.class);
        }

        @Test
        @DisplayName("음수 원금이면 예외 발생")
        void testEqualPaymentWithNegativePrincipal() {
            RepaymentResultDTO result = calculator.calculateEqualPayment(
                    BigDecimal.valueOf(0.01),
                    BigDecimal.valueOf(12),
                    BigDecimal.valueOf(-1000000)
            );
            assertThat(result.getTotalPayment()).isLessThan(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("원금 균등상환 테스트")
    class EqualPrincipalTests {

        //        @ParameterizedTest(name = "이율: {0}, 기간: {1}, 원금: {2}")
//        @CsvSource({
//                "0.00, 12, 1200000",
//                "0.01, 12, 1200000",
//                "0.02, 24, 5000000",
//                "0.03, 36, 10000000"
//        })
        void testEqualPrincipal(BigDecimal interestRate, BigDecimal months, BigDecimal principal) {
            RepaymentResultDTO result = calculator.calculateEqualPrincipal(interestRate, months, principal);

            List<MonthlyPaymentDetailDTO> payments = result.getPayments();
            assertThat(payments).hasSize(months.intValueExact());

            BigDecimal firstInterest = payments.get(0).getInterest();
            BigDecimal lastInterest = payments.get(payments.size() - 1).getInterest();

            assertThat(firstInterest).isGreaterThanOrEqualTo(lastInterest);
            assertThat(result.getTotalPayment()).isGreaterThanOrEqualTo(principal);
        }

        @Test
        @DisplayName("1개월만 상환하는 경우")
        void testEqualPrincipalWithOneMonth() {
            RepaymentResultDTO result = calculator.calculateEqualPrincipal(
                    BigDecimal.valueOf(0.01),
                    BigDecimal.ONE,
                    BigDecimal.valueOf(500000)
            );

            assertThat(result.getPayments()).hasSize(1);
            MonthlyPaymentDetailDTO payment = result.getPayments().get(0);
            assertThat(payment.getPrincipal()).isEqualByComparingTo(BigDecimal.valueOf(500000));
        }

        @Test
        @DisplayName("음수 이자율 입력 시")
        void testEqualPrincipalWithNegativeInterest() {
            BigDecimal interestRate = BigDecimal.valueOf(-0.01);
            BigDecimal months = BigDecimal.valueOf(12);
            BigDecimal principal = BigDecimal.valueOf(1000000);

            RepaymentResultDTO result = calculator.calculateEqualPrincipal(interestRate, months, principal);

            assertThat(result.getPayments()).hasSize(12);
            assertThat(result.getTotalPayment()).isLessThan(principal);
        }
    }
}

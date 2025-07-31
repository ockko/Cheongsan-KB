//package cheongsan.domain.simulator.service;
//
//import cheongsan.domain.simulator.dto.MonthlyPaymentDetailDTO;
//import cheongsan.domain.simulator.dto.PaymentResultDTO;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.assertj.core.api.AssertionsForClassTypes.within;
//
//class LoanRepaymentCalculatorTest {
//
//    private final LoanRepaymentCalculator calculator = new LoanRepaymentCalculator();
//
//
//    @DisplayName("원리금 균등상환 테스트")
//    @Test
//    void testEqualPayment() {
//        // given
//        BigDecimal interestRate = new BigDecimal("0.01");  // 1% 월이자
//        BigDecimal months = new BigDecimal("12");           // 12개월 상환
//        BigDecimal remainingPrincipal = new BigDecimal("1200000"); // 1,200,000원 원금
//
//        // when
//        PaymentResultDTO result = calculator.calculateEqualPayment(interestRate, months, remainingPrincipal);
//
//        // then
//        assertThat(result.getPayments()).hasSize(months.intValueExact());
//        assertThat(result.getTotalPayment()).isGreaterThanOrEqualTo(remainingPrincipal);
//
//        // 첫 달 정보 확인
//        MonthlyPaymentDetailDTO first = result.getPayments().get(0);
//        assertThat(first.getPrincipal()).isGreaterThan(BigDecimal.ZERO);
//        assertThat(first.getTotalPayment()).isGreaterThan(BigDecimal.ZERO);
//
//        // 모든 월 납입금이 동일한지 확인
//        BigDecimal expectedMonthlyPayment = first.getTotalPayment();
//        for (MonthlyPaymentDetailDTO payment : result.getPayments()) {
//            assertThat(payment.getTotalPayment()).isEqualByComparingTo(expectedMonthlyPayment);
//        }
//
//        // 원금 합계가 남은 원금과 일치하는지 확인 (오차 허용)
//        BigDecimal principalSum = result.getPayments().stream()
//                .map(MonthlyPaymentDetailDTO::getPrincipal)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        assertThat(principalSum).isCloseTo(remainingPrincipal, within(new BigDecimal("1")));
//
//        // 이자 합계가 0 이상인지 확인
//        BigDecimal interestSum = result.getPayments().stream()
//                .map(MonthlyPaymentDetailDTO::getInterest)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        assertThat(interestSum).isGreaterThanOrEqualTo(BigDecimal.ZERO);
//
//        System.out.println(result.getTotalPayment());
//    }
//
//    @DisplayName("원금 균등 상환")
//    @Test
//    void testCalculateEqualPrincipal_NormalCase() {
//        // given
//        BigDecimal interestRate = new BigDecimal("0.01");  // 1% 월이자
//        BigDecimal months = new BigDecimal("12");           // 12개월 상환
//        BigDecimal remainingPrincipal = new BigDecimal("1200000"); // 1,200,000원 원금
//
//        // when
//        PaymentResultDTO result = calculator.calculateEqualPrincipal(interestRate, months, remainingPrincipal);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getPayments()).hasSize(12);
//
//        // 첫 달 이자: 1,200,000 * 0.01 = 12,000원
//        assertThat(result.getPayments().get(0).getInterest())
//                .isEqualByComparingTo(new BigDecimal("12000").setScale(0, RoundingMode.HALF_UP));
//
//        // 매월 고정 원금: 1,200,000 / 12 = 100,000원
//        assertThat(result.getPayments().get(0).getPrincipal())
//                .isEqualByComparingTo(new BigDecimal("100000").setScale(0, RoundingMode.HALF_UP));
//
//        // 첫 달 총 납입금: 100,000 + 12,000 = 112,000원
//        assertThat(result.getPayments().get(0).getTotalPayment())
//                .isEqualByComparingTo(new BigDecimal("112000").setScale(0, RoundingMode.HALF_UP));
//
//        // 마지막 달 이자: 원금은 100,000씩 줄어듦 -> 100,000 * 0.01 = 1,000원
//        assertThat(result.getPayments().get(11).getInterest())
//                .isEqualByComparingTo(new BigDecimal("1000").setScale(0, RoundingMode.HALF_UP));
//
//        // 마지막 달 원금 고정 100,000원
//        assertThat(result.getPayments().get(11).getPrincipal())
//                .isEqualByComparingTo(new BigDecimal("100000").setScale(0, RoundingMode.HALF_UP));
//
//        // 마지막 달 총 납입금: 100,000 + 1,000 = 101,000원
//        assertThat(result.getPayments().get(11).getTotalPayment())
//                .isEqualByComparingTo(new BigDecimal("101000").setScale(0, RoundingMode.HALF_UP));
//
//        // 총 납입금은 원금 + 이자 합 (대략)
//        BigDecimal expectedTotal = BigDecimal.valueOf(12 * 100_000) // 원금 1,200,000
//                .add(new BigDecimal("78000")); // 12,000 + 11,000 + ... + 1,000 = 약 78,000원 이자 합산 예상 (대략)
//        // 실제 총 납입금이 기대값과 비슷한지 확인 (허용 오차 ±1000원)
//        assertThat(result.getTotalPayment()).isCloseTo(expectedTotal, within(new BigDecimal("1000")));
//        System.out.println(result.getTotalPayment());
//    }
//
//    @DisplayName("원금 균등 상환 - 이자율 0인경우")
//    @Test
//    void testCalculateEqualPrincipal_ZeroInterest() {
//        // given
//        BigDecimal interestRate = BigDecimal.ZERO;
//        BigDecimal months = new BigDecimal("6");
//        BigDecimal remainingPrincipal = new BigDecimal("600000");
//
//        // when
//        PaymentResultDTO result = calculator.calculateEqualPrincipal(interestRate, months, remainingPrincipal);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getPayments()).hasSize(6);
//
//        // 매월 원금 100,000원, 이자 0원, 총납입금 100,000원
//        result.getPayments().forEach(payment -> {
//            assertThat(payment.getPrincipal()).isEqualByComparingTo(new BigDecimal("100000"));
//            assertThat(payment.getInterest()).isEqualByComparingTo(BigDecimal.ZERO);
//            assertThat(payment.getTotalPayment()).isEqualByComparingTo(new BigDecimal("100000"));
//        });
//
//        // 총 납입금 = 600,000원 (원금만)
//        assertThat(result.getTotalPayment()).isEqualByComparingTo(new BigDecimal("600000"));
//    }
//
//    @Test
//    @DisplayName("0개월이면 예외 발생")
//    void testEqualPaymentWithZeroMonth() {
//        assertThatThrownBy(() ->
//                calculator.calculateEqualPayment(BigDecimal.valueOf(0.01), BigDecimal.ZERO, BigDecimal.valueOf(1000000))
//        ).isInstanceOf(ArithmeticException.class);
//    }
//
//    @Test
//    @DisplayName("음수 원금이면 예외 발생")
//    void testEqualPaymentWithNegativePrincipal() {
//        PaymentResultDTO result = calculator.calculateEqualPayment(
//                BigDecimal.valueOf(0.01),
//                BigDecimal.valueOf(12),
//                BigDecimal.valueOf(-1000000)
//        );
//        assertThat(result.getTotalPayment()).isLessThan(BigDecimal.ZERO);
//    }
//
//
//    @Test
//    @DisplayName("1개월만 상환하는 경우")
//    void testEqualPrincipalWithOneMonth() {
//        PaymentResultDTO result = calculator.calculateEqualPrincipal(
//                BigDecimal.valueOf(0.01),
//                BigDecimal.ONE,
//                BigDecimal.valueOf(500000)
//        );
//
//        assertThat(result.getPayments()).hasSize(1);
//        MonthlyPaymentDetailDTO payment = result.getPayments().get(0);
//        assertThat(payment.getPrincipal()).isEqualByComparingTo(BigDecimal.valueOf(500000));
//    }
//
//    @Test
//    @DisplayName("음수 이자율 입력 시")
//    void testEqualPrincipalWithNegativeInterest() {
//        BigDecimal interestRate = BigDecimal.valueOf(-0.01);
//        BigDecimal months = BigDecimal.valueOf(12);
//        BigDecimal principal = BigDecimal.valueOf(1000000);
//
//        PaymentResultDTO result = calculator.calculateEqualPrincipal(interestRate, months, principal);
//
//        assertThat(result.getPayments()).hasSize(12);
//        assertThat(result.getTotalPayment()).isLessThan(principal);
//    }
//
//
//    @DisplayName("만기 일시 상환 + 중도 상환")
//    @Test
//    void testCalculateLumpSumRepaymentWithPrepayment() {
//        // given
//        BigDecimal principal = new BigDecimal("10000000");
//        BigDecimal annualRate = new BigDecimal("0.12");
//        int loanPeriodMonths = 12;
//        LocalDate loanStartDate = LocalDate.of(2025, 1, 1);
//        BigDecimal monthlyPrepayment = new BigDecimal("100000");
//
//        // when
//        PaymentResultDTO result = calculator.calculateLumpSumRepaymentWithPrepayment(
//                principal, annualRate, loanPeriodMonths, monthlyPrepayment
//        );
//
//        // then
//        List<MonthlyPaymentDetailDTO> payments = result.getPayments();
//        BigDecimal totalPayment = result.getTotalPayment();
//
//        assertThat(payments).hasSize(loanPeriodMonths);
//        assertThat(totalPayment).isGreaterThan(BigDecimal.ZERO);
//        assertThat(payments.get(payments.size() - 1).getPrincipal())
//                .isGreaterThanOrEqualTo(BigDecimal.valueOf(100000)); // 마지막 달엔 남은 원금 상환
//
//        // 출력 (선택)
//        System.out.println("총 상환액: " + totalPayment);
//        payments.forEach(payment -> System.out.printf(
//                "%2d개월차 - 원금: %,d / 이자: %,d / 총납입: %,d%n",
//                payment.getMonth(),
//                payment.getPrincipal().intValue(),
//                payment.getInterest().intValue(),
//                payment.getTotalPayment().intValue()
//        ));
//
//    }
//
//    @DisplayName("만기 일시 상환")
//    @Test
//    void testLumpSumRepaymentCalculation() {
//        // Given
//        BigDecimal principal = BigDecimal.valueOf(10_000_000); // 1,000만원
//        BigDecimal annualRate = BigDecimal.valueOf(0.12);       // 연이율 6%
//        int loanPeriodMonths = 12;                               // 12개월
//        LocalDate startDate = LocalDate.of(2025, 1, 1);
//
//        // When
//        PaymentResultDTO result = calculator.calculateLumpSumRepayment(principal, annualRate, loanPeriodMonths, startDate);
//
//        // Then
//        BigDecimal expectedMonthlyInterest = principal.multiply(annualRate).divide(BigDecimal.valueOf(12), calculator.MATH_CONTEXT);
//        BigDecimal expectedTotalInterest = expectedMonthlyInterest.multiply(BigDecimal.valueOf(12));
//        BigDecimal expectedTotalPayment = expectedTotalInterest.add(principal);
//
//        assertThat(result.getTotalPayment()).isEqualByComparingTo(expectedTotalPayment.setScale(0, BigDecimal.ROUND_HALF_UP));
//
//        List<MonthlyPaymentDetailDTO> payments = result.getPayments();
//
//        assertThat(payments).hasSize(12);
//
//        for (int i = 0; i < 11; i++) {
//            MonthlyPaymentDetailDTO payment = payments.get(i);
//            assertThat(payment.getPrincipal()).isEqualByComparingTo(BigDecimal.ZERO);
//            assertThat(payment.getInterest()).isEqualByComparingTo(expectedMonthlyInterest.setScale(0, BigDecimal.ROUND_HALF_UP));
//        }
//
//        MonthlyPaymentDetailDTO lastPayment = payments.get(11);
//        System.out.println(result.getTotalPayment());
//        assertThat(lastPayment.getPrincipal()).isEqualByComparingTo(principal.setScale(0, BigDecimal.ROUND_HALF_UP));
//        assertThat(lastPayment.getInterest()).isEqualByComparingTo(expectedMonthlyInterest.setScale(0, BigDecimal.ROUND_HALF_UP));
//    }
//}

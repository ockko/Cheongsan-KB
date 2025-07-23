package cheongsan.common.util;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
class LoanCalculatorTest {

    private LoanCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new LoanCalculator();
    }

    @Test
    @DisplayName("원리금균등분할상환 월 상환액을 정확히 계산해야 한다")
    void calculateEqualPrincipalAndInterest() {
        // given
        LoanCalculator.RepaymentMethod method = LoanCalculator.RepaymentMethod.EQUAL_PRINCIPAL_INTEREST;
        BigDecimal originalAmount = new BigDecimal("10000000.00"); // 원금: 1000만원
        BigDecimal interestRate = new BigDecimal("5.00");          // 연이율: 5%
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = startDate.plusYears(1);             // 대출 기간: 12개월

        // when
        BigDecimal monthlyPayment = calculator.calculateMonthlyPayment(
                method,
                originalAmount,
                originalAmount, // currentBalance는 이 방식에서 사용되지 않음
                interestRate,
                startDate,
                endDate
        );

        // then
        // 1000만원을 연 5%로 12개월 원리금균등상환 시 월 상환액은 856,074.82원
        assertEquals(new BigDecimal("856074.82"), monthlyPayment, "원리금균등상환액이 일치해야 합니다.");
    }

    @Test
    @DisplayName("원금균등분할상환 첫 달 상환액을 정확히 계산해야 한다")
    void calculateEqualPrincipal() {
        // given
        LoanCalculator.RepaymentMethod method = LoanCalculator.RepaymentMethod.EQUAL_PRINCIPAL;
        BigDecimal originalAmount = new BigDecimal("12000000.00"); // 원금: 1200만원
        BigDecimal currentBalance = new BigDecimal("12000000.00"); // 현재 잔액: 1200만원 (첫 달)
        BigDecimal interestRate = new BigDecimal("6.00");          // 연이율: 6%
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = startDate.plusYears(1);             // 대출 기간: 12개월

        // when
        BigDecimal monthlyPayment = calculator.calculateMonthlyPayment(
                method,
                originalAmount,
                currentBalance,
                interestRate,
                startDate,
                endDate
        );

        // then
        // 매월 상환 원금: 1200만원 / 12개월 = 100만원
        // 첫 달 이자: 1200만원 * (6% / 12) = 60,000원
        // 첫 달 총상환액 = 1,060,000원
        assertEquals(new BigDecimal("1060000.00"), monthlyPayment, "원금균등상환액(첫 달)이 일치해야 합니다.");
    }

    @Test
    @DisplayName("만기일시상환 월 상환 이자를 정확히 계산해야 한다")
    void calculateBulletRepayment() {
        // given
        LoanCalculator.RepaymentMethod method = LoanCalculator.RepaymentMethod.BULLET_REPAYMENT;
        BigDecimal originalAmount = new BigDecimal("10000000.00"); // 원금: 1000만원
        BigDecimal interestRate = new BigDecimal("5.00");          // 연이율: 5%
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = startDate.plusYears(1);

        // when
        BigDecimal monthlyPayment = calculator.calculateMonthlyPayment(
                method,
                originalAmount,
                originalAmount, // currentBalance는 이 방식에서 사용되지 않음
                interestRate,
                startDate,
                endDate
        );

        // then
        // 매월 이자: 1000만원 * (5% / 12) = 41,666.67원
        assertEquals(new BigDecimal("41666.67"), monthlyPayment, "만기일시상환 이자액이 일치해야 합니다.");
    }

    @Test
    @DisplayName("대출 기간이 0이거나 음수일 경우 0을 반환해야 한다")
    void calculate_InvalidPeriod_ShouldReturnZero() {
        // given
        LoanCalculator.RepaymentMethod method = LoanCalculator.RepaymentMethod.EQUAL_PRINCIPAL_INTEREST;
        BigDecimal originalAmount = new BigDecimal("10000000.00");
        BigDecimal annualRate = new BigDecimal("5.00");
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 1); // 대출 기간이 0개월

        // when
        BigDecimal monthlyPayment = calculator.calculateMonthlyPayment(method, originalAmount, originalAmount, annualRate, startDate, endDate);

        // then
        assertEquals(BigDecimal.ZERO.setScale(2), monthlyPayment, "기간이 유효하지 않으면 0을 반환해야 합니다.");
    }
}
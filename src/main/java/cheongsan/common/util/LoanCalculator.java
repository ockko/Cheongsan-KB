package cheongsan.common.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class LoanCalculator {

    public enum RepaymentMethod {
        EQUAL_PRINCIPAL_INTEREST, // 원리금균등상환
        EQUAL_PRINCIPAL,          // 원금균등상환
        BULLET_REPAYMENT          // 만기일시상환
    }

    public BigDecimal calculateMonthlyPayment(
            RepaymentMethod method,
            BigDecimal originalAmount,
            BigDecimal currentBalance,
            BigDecimal interestRate,
            LocalDate loanStartDate,
            LocalDate loanEndDate
    ) {
        // 총 대출 개월 수 계산
        long totalMonths = ChronoUnit.MONTHS.between(loanStartDate, loanEndDate);
        if (totalMonths <= 0) {
            return BigDecimal.ZERO.setScale(2);
        }

        switch (method) {
            case EQUAL_PRINCIPAL_INTEREST:
                return calculateEqualPrincipalAndInterest(originalAmount, interestRate, (int) totalMonths);
            case EQUAL_PRINCIPAL:
                return calculateEqualPrincipal(originalAmount, currentBalance, interestRate, (int) totalMonths);
            case BULLET_REPAYMENT:
                return calculateBulletRepayment(originalAmount, interestRate);
            default:
                throw new IllegalArgumentException("지원하지 않는 상환방식입니다.");
        }
    }

    // 원리금균등분할상환
    private BigDecimal calculateEqualPrincipalAndInterest(BigDecimal principal, BigDecimal interestRate, int totalMonths) {
        // 연이율을 월이율로 변환 (소수점 10자리까지 계산)
        BigDecimal monthlyRate = interestRate.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
                .divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);

        // 이자가 없는 경우, 원금을 개월 수로 나눈 값을 반환
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(new BigDecimal(totalMonths), 2, RoundingMode.HALF_UP);
        }

        // 공식의 (1+r)^n 부분 계산
        BigDecimal rateFactor = (BigDecimal.ONE.add(monthlyRate)).pow(totalMonths);

        // 분자 계산: P * r * (1+r)^n
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(rateFactor);

        // 분모 계산: (1+r)^n - 1
        BigDecimal denominator = rateFactor.subtract(BigDecimal.ONE);

        // 분모가 0인 경우 (이례적 상황 방지)
        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(2);
        }

        // 월 상환액 계산: 분자 / 분모 (원 단위로 반올림)
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    // 원금균등분할상환
    private BigDecimal calculateEqualPrincipal(BigDecimal principal, BigDecimal currentBalance, BigDecimal interestRate, int totalMonths) {
        // 매월 상환해야 하는 고정 원금
        BigDecimal monthlyPrincipalPayment = principal.divide(new BigDecimal(totalMonths), 2, RoundingMode.HALF_UP);

        // 연이율을 월이율로 변환
        BigDecimal monthlyRate = interestRate.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
                .divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);

        // 이번 달에 내야 할 이자 (현재 남은 원금 기준)
        BigDecimal monthlyInterest = currentBalance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);

        // 이번 달 총상환액 = 고정 원금 + 이번 달 이자
        return monthlyPrincipalPayment.add(monthlyInterest);
    }

    // 만기일시상환
    private BigDecimal calculateBulletRepayment(BigDecimal principal, BigDecimal interestRate) {
        // 연이율을 월이율로 변환
        BigDecimal monthlyRate = interestRate.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
                .divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);

        // 이번 달에 내야 할 이자
        return principal.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
    }
}

package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.LoanDTO;
import cheongsan.domain.simulator.dto.MonthlyPaymentDetailDTO;
import cheongsan.domain.simulator.dto.Scenario;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class MonthlyRepaymentCalculator {

    public static final MathContext MATH_CONTEXT = new MathContext(15, RoundingMode.HALF_UP);

    public MonthlyPaymentDetailDTO simulateMonthlyEqualPayment(
            LoanDTO loan,
            LocalDate date,
            BigDecimal extraPayment,
            Scenario scenario
    ) {
        BigDecimal monthlyInterestRate = loan.getMonthlyInterestRate();
        BigDecimal remainingPrincipal = loan.getPrincipal();

        long remainingMonths = loan.getRemainingMonths(date);
        if (remainingMonths <= 0 || remainingPrincipal.compareTo(BigDecimal.ZERO) <= 0) {
            return new MonthlyPaymentDetailDTO(0,
                    BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, date, BigDecimal.ZERO);
        }

        BigDecimal one = BigDecimal.ONE;
        BigDecimal pow = (one.add(monthlyInterestRate)).pow((int) remainingMonths, MATH_CONTEXT);

        // 원리금 균등상환 방식으로 월 납입액 계산
        BigDecimal monthlyPayment = remainingPrincipal.multiply(
                        monthlyInterestRate.multiply(pow), MATH_CONTEXT)
                .divide(pow.subtract(one, MATH_CONTEXT), MATH_CONTEXT);

        // 이자 = 남은 원금 * 월 이자율
        BigDecimal interest = remainingPrincipal.multiply(monthlyInterestRate, MATH_CONTEXT);

        // 기본 원금 상환 = 납입금 - 이자
        BigDecimal principal = monthlyPayment.subtract(interest, MATH_CONTEXT);

        // 초과상환 및 수수료 계산
        BigDecimal actualExtraPayment = BigDecimal.ZERO;
        BigDecimal prepaymentFee = BigDecimal.ZERO;

        if (scenario == Scenario.WITH_PREPAYMENT
                && extraPayment != null
                && extraPayment.compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal maxExtraPayment = remainingPrincipal.subtract(principal).max(BigDecimal.ZERO);
            actualExtraPayment = extraPayment.min(maxExtraPayment);
            prepaymentFee = calculatePrepaymentFee(actualExtraPayment, date, loan);
        }

        // 총 납입액 = 월납입금 + 초과상환 + 수수료
        BigDecimal totalMonthlyPayment = monthlyPayment.add(actualExtraPayment).add(prepaymentFee);

        BigDecimal updatedRemaining = remainingPrincipal
                .subtract(principal)
                .subtract(actualExtraPayment)
                .max(BigDecimal.ZERO);


        return new MonthlyPaymentDetailDTO(
                1, // 단일 월 계산이므로 항상 1
                principal.setScale(0, RoundingMode.HALF_UP),
                interest.setScale(0, RoundingMode.HALF_UP),
                actualExtraPayment.setScale(0, RoundingMode.HALF_UP),
                prepaymentFee.setScale(0, RoundingMode.HALF_UP),
                totalMonthlyPayment.setScale(0, RoundingMode.HALF_UP),
                date,
                updatedRemaining.setScale(0, RoundingMode.HALF_UP)
        );
    }

    public MonthlyPaymentDetailDTO simulateMonthlyEqualPrincipal(
            LoanDTO loan,
            LocalDate date,
            BigDecimal extraPayment,
            Scenario scenario
    ) {
        BigDecimal monthlyInterestRate = loan.getMonthlyInterestRate();

        // 남은 원금 (date 시점 기준)
        BigDecimal remainingPrincipal = loan.getPrincipal();

        // 남은 상환 개월 수 계산 (date 기준)
        long remainingMonths = loan.getRemainingMonths(date);
        if (remainingMonths <= 0 || remainingPrincipal.compareTo(BigDecimal.ZERO) <= 0) {
            return new MonthlyPaymentDetailDTO(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, date, BigDecimal.ZERO);
        }

        // 이번 달 상환 월 번호 계산 (예: 1부터 시작)
        long monthNumber = loan.getRemainingMonths(loan.getStartDate()) - remainingMonths + 1;

        // 고정 원금 (매달 동일하게 상환)
        BigDecimal monthlyPrincipal = remainingPrincipal.divide(BigDecimal.valueOf(remainingMonths), MATH_CONTEXT);

        // 이번 달 이자 = 남은 원금 * 이자율
        BigDecimal interest = remainingPrincipal.multiply(monthlyInterestRate, MATH_CONTEXT);

        // 중도상환 계산
        BigDecimal actualExtraPayment = BigDecimal.ZERO;
        BigDecimal prepaymentFee = BigDecimal.ZERO;
        if (scenario == Scenario.WITH_PREPAYMENT && extraPayment != null && extraPayment.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal maxExtraPayment = remainingPrincipal.subtract(monthlyPrincipal.add(extraPayment)).max(BigDecimal.ZERO);
            actualExtraPayment = extraPayment.min(maxExtraPayment);
            prepaymentFee = calculatePrepaymentFee(actualExtraPayment, date, loan);
        }

        // 총 납입액 = 고정 원금 + 이자 + 중도상환 + 수수료
        BigDecimal totalPayment = monthlyPrincipal.add(interest).add(actualExtraPayment).add(prepaymentFee);

        BigDecimal updatedRemaining = remainingPrincipal.subtract(monthlyPrincipal.add(actualExtraPayment)).max(BigDecimal.ZERO);

        return new MonthlyPaymentDetailDTO(
                monthNumber,
                monthlyPrincipal.setScale(0, RoundingMode.HALF_UP),
                interest.setScale(0, RoundingMode.HALF_UP),
                actualExtraPayment.setScale(0, RoundingMode.HALF_UP),
                prepaymentFee.setScale(0, RoundingMode.HALF_UP),
                totalPayment.setScale(0, RoundingMode.HALF_UP),
                date,
                updatedRemaining.setScale(0, RoundingMode.HALF_UP)
        );
    }

    public MonthlyPaymentDetailDTO equalPaymentWithPrePayment(
            BigDecimal monthlyPrepayment,
            LoanDTO loan,
            LocalDate date
    ) {
        BigDecimal one = BigDecimal.ONE;
        BigDecimal remainingPrincipal = loan.getPrincipal();

        long remainingMonths = loan.getRemainingMonths(date);
        BigDecimal monthlyInterestRate = loan.getMonthlyInterestRate();

        if (remainingPrincipal.compareTo(BigDecimal.ZERO) <= 0 || remainingMonths <= 0) {
            return new MonthlyPaymentDetailDTO(
                    1,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    date,
                    BigDecimal.ZERO
            );
        }

        // 고정 월 상환금 계산
        BigDecimal pow = (one.add(monthlyInterestRate)).pow((int) remainingMonths, MATH_CONTEXT);
        BigDecimal monthlyPayment = remainingPrincipal.multiply(
                monthlyInterestRate.multiply(pow), MATH_CONTEXT
        ).divide(
                pow.subtract(one, MATH_CONTEXT), MATH_CONTEXT
        );

        // 이번 달 이자
        BigDecimal nowInterest = remainingPrincipal.multiply(monthlyInterestRate, MATH_CONTEXT);
        // 이번 달 원금 상환분
        BigDecimal nowPrincipal = monthlyPayment.subtract(nowInterest, MATH_CONTEXT);

        BigDecimal actualMonthlyPrincipal = nowPrincipal.min(remainingPrincipal);
        BigDecimal actualPrepayment = monthlyPrepayment.min(
                remainingPrincipal.subtract(actualMonthlyPrincipal, MATH_CONTEXT).max(BigDecimal.ZERO)
        );

        BigDecimal fee = BigDecimal.ZERO;
        if (actualPrepayment.compareTo(BigDecimal.ZERO) > 0) {
            fee = calculatePrepaymentFee(actualPrepayment, date, loan);
        }

        BigDecimal totalMonthlyPayment = monthlyPayment
                .add(actualPrepayment, MATH_CONTEXT)
                .add(fee, MATH_CONTEXT);

        remainingPrincipal = remainingPrincipal
                .subtract(actualMonthlyPrincipal, MATH_CONTEXT)
                .subtract(actualPrepayment, MATH_CONTEXT)
                .max(BigDecimal.ZERO);

        return new MonthlyPaymentDetailDTO(
                1,
                actualMonthlyPrincipal.setScale(0, RoundingMode.HALF_UP),
                nowInterest.setScale(0, RoundingMode.HALF_UP),
                actualPrepayment.setScale(0, RoundingMode.HALF_UP),
                fee.setScale(0, RoundingMode.HALF_UP),
                totalMonthlyPayment.setScale(0, RoundingMode.HALF_UP),
                date,
                remainingPrincipal.setScale(0, RoundingMode.HALF_UP)
        );
    }

    public MonthlyPaymentDetailDTO equalPrincipalWithPrePayment(
            BigDecimal monthlyPrepayment,
            LoanDTO loan,
            LocalDate date
    ) {
        BigDecimal remainingPrincipal = loan.getPrincipal();
        BigDecimal monthlyInterestRate = loan.getMonthlyInterestRate();
        long remainingMonths = loan.getRemainingMonths(date);

        if (remainingPrincipal.compareTo(BigDecimal.ZERO) == 0 || remainingMonths <= 0) {
            // 상환 완료된 경우, 모두 0으로 반환
            return new MonthlyPaymentDetailDTO(
                    0,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    date,
                    BigDecimal.ZERO
            );
        }

        // 고정 원금 상환액
        BigDecimal fixedMonthlyPrincipal = remainingPrincipal
                .divide(BigDecimal.valueOf(remainingMonths), MATH_CONTEXT);

        // 이번 달 실제 원금 상환액 (남은 원금보다 크지 않도록)
        BigDecimal actualMonthlyPrincipal = fixedMonthlyPrincipal.min(remainingPrincipal);

        // 중도상환금 (고정 원금 상환 후 남은 원금 내에서 적용)
        BigDecimal maxAvailablePrepayment = remainingPrincipal.subtract(actualMonthlyPrincipal, MATH_CONTEXT);
        BigDecimal actualPrepayment = monthlyPrepayment.min(maxAvailablePrepayment.max(BigDecimal.ZERO));

        // 이자 계산
        BigDecimal interest = remainingPrincipal.multiply(monthlyInterestRate, MATH_CONTEXT);

        // 수수료 계산
        BigDecimal fee = BigDecimal.ZERO;
        if (actualPrepayment.compareTo(BigDecimal.ZERO) > 0) {
            fee = calculatePrepaymentFee(actualPrepayment, date, loan);
        }

        // 총 납부금 = 원금 + 이자 + 중도상환금 + 수수료
        BigDecimal totalPayment = actualMonthlyPrincipal
                .add(interest, MATH_CONTEXT)
                .add(actualPrepayment, MATH_CONTEXT)
                .add(fee, MATH_CONTEXT);

        remainingPrincipal = remainingPrincipal
                .subtract(actualMonthlyPrincipal, MATH_CONTEXT)
                .subtract(actualPrepayment, MATH_CONTEXT)
                .max(BigDecimal.ZERO);

        return new MonthlyPaymentDetailDTO(
                1, // 월 번호 (필요 시 동적으로 조정)
                actualMonthlyPrincipal.setScale(0, RoundingMode.HALF_UP),
                interest.setScale(0, RoundingMode.HALF_UP),
                actualPrepayment.setScale(0, RoundingMode.HALF_UP),
                fee.setScale(0, RoundingMode.HALF_UP),
                totalPayment.setScale(0, RoundingMode.HALF_UP),
                date,
                remainingPrincipal.setScale(0, RoundingMode.HALF_UP)
        );
    }

    public MonthlyPaymentDetailDTO calculateLumpSumRepaymentMonthly(LoanDTO loan, LocalDate date) {
        BigDecimal monthlyRate = loan.getMonthlyInterestRate();
        BigDecimal interest = loan.getPrincipal().multiply(monthlyRate, MATH_CONTEXT);
        BigDecimal remainingPrincipal = loan.getPrincipal();
        long remainingMonths = loan.getRemainingMonths(date);
        if (remainingMonths <= 0) {
            // 이미 상환 완료된 경우, 모두 0 반환
            return new MonthlyPaymentDetailDTO(
                    0,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    date,
                    BigDecimal.ZERO
            );
        }

        BigDecimal principalPayment = BigDecimal.ZERO;
        BigDecimal totalMonthlyPayment = interest;

        if (remainingMonths == 1) {
            // 마지막 달에 원금도 상환
            principalPayment = loan.getPrincipal();
            totalMonthlyPayment = totalMonthlyPayment.add(principalPayment, MATH_CONTEXT);
            remainingPrincipal = BigDecimal.ZERO;
        }

        return new MonthlyPaymentDetailDTO(
                1, // 월 번호 (필요 시 동적 조절 가능)
                principalPayment.setScale(0, RoundingMode.HALF_UP),
                interest.setScale(0, RoundingMode.HALF_UP),
                BigDecimal.ZERO, // prepayment 없음
                BigDecimal.ZERO, // prepaymentFee 없음
                totalMonthlyPayment.setScale(0, RoundingMode.HALF_UP),
                date,
                remainingPrincipal.setScale(0, RoundingMode.HALF_UP)
        );
    }

    public MonthlyPaymentDetailDTO calculateLumpSumRepaymentWithPrepaymentMonthly(
            BigDecimal monthlyPrepayment, LoanDTO loan, LocalDate date) {

        BigDecimal monthlyInterestRate = loan.getMonthlyInterestRate();
        BigDecimal remainingPrincipal = loan.getPrincipal();

        long remainingMonths = loan.getRemainingMonths(date);
        if (remainingMonths <= 0) {
            // 상환 완료된 경우
            return new MonthlyPaymentDetailDTO(
                    0,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    date,
                    BigDecimal.ZERO
            );
        }

        BigDecimal interest = remainingPrincipal.multiply(monthlyInterestRate, MATH_CONTEXT);
        BigDecimal principalPayment = BigDecimal.ZERO;
        BigDecimal actualPrepayment = BigDecimal.ZERO;
        BigDecimal prepaymentFee = BigDecimal.ZERO;

        // 중도상환 적용
        if (monthlyPrepayment.compareTo(BigDecimal.ZERO) > 0) {
            if (monthlyPrepayment.compareTo(remainingPrincipal) >= 0) {
                principalPayment = remainingPrincipal;
                remainingPrincipal = BigDecimal.ZERO;
                actualPrepayment = principalPayment;
            } else {
                principalPayment = monthlyPrepayment;
                remainingPrincipal = remainingPrincipal.subtract(monthlyPrepayment, MATH_CONTEXT);
                actualPrepayment = monthlyPrepayment;
            }
            // 중도상환 수수료 계산
            prepaymentFee = calculatePrepaymentFee(actualPrepayment, date, loan);
        }

        // 만기 월이면 남은 원금까지 포함
        if (remainingMonths == 1 && remainingPrincipal.compareTo(BigDecimal.ZERO) > 0) {
            principalPayment = principalPayment.add(remainingPrincipal, MATH_CONTEXT);
            remainingPrincipal = BigDecimal.ZERO;
        }

        BigDecimal totalMonthlyPayment = interest.add(principalPayment, MATH_CONTEXT).add(prepaymentFee, MATH_CONTEXT);

        return new MonthlyPaymentDetailDTO(
                1, // 월 번호 (필요 시 조절)
                principalPayment.setScale(0, RoundingMode.HALF_UP),
                interest.setScale(0, RoundingMode.HALF_UP),
                actualPrepayment.setScale(0, RoundingMode.HALF_UP),
                prepaymentFee.setScale(0, RoundingMode.HALF_UP),
                totalMonthlyPayment.setScale(0, RoundingMode.HALF_UP),
                date,
                remainingPrincipal.setScale(0, RoundingMode.HALF_UP)
        );
    }


    public BigDecimal calculatePrepaymentFee(BigDecimal prepaymentPrincipal, LocalDate repaymentDate, LoanDTO loan) {
        final BigDecimal ONE_PERCENT = new BigDecimal("0.01");
        final long THREE_YEARS_IN_DAYS = 365 * 3;

        LocalDate startDate = loan.getStartDate();
        LocalDate endDate = loan.getEndDate();
        BigDecimal prepaymentFeeRate = loan.getPrepaymentFeeRate();

        long totalLoanDays = ChronoUnit.DAYS.between(startDate, endDate);
        long usedDays = ChronoUnit.DAYS.between(startDate, repaymentDate);
        long remainingDays = ChronoUnit.DAYS.between(repaymentDate, endDate);

        // ① 3년(1095일) 초과 시 면제
        if (usedDays >= THREE_YEARS_IN_DAYS || totalLoanDays <= 0) {
            return BigDecimal.ZERO;
        }

        // ② 대출일로부터 30일 미만 → 30일로 간주
        if (usedDays < 30) {
            usedDays = 30;
            totalLoanDays = 30;
            remainingDays = totalLoanDays - usedDays;
            if (remainingDays < 0) remainingDays = 0;
        }

        // ③ 대출 1년 이상인지 여부
        boolean isLongTerm = totalLoanDays >= 365;

        BigDecimal fee;

        if (isLongTerm) {
            // 만기 1년 이상
            BigDecimal variableRate = prepaymentFeeRate.subtract(ONE_PERCENT);
            BigDecimal fixedFee = prepaymentPrincipal.multiply(ONE_PERCENT, MATH_CONTEXT);
            BigDecimal variableFee = prepaymentPrincipal
                    .multiply(variableRate, MATH_CONTEXT)
                    .multiply(new BigDecimal(remainingDays), MATH_CONTEXT)
                    .divide(new BigDecimal(totalLoanDays - 30), MATH_CONTEXT);

            fee = fixedFee.add(variableFee);
        } else {
            // 만기 1년 미만
            fee = prepaymentPrincipal
                    .multiply(prepaymentFeeRate, MATH_CONTEXT)
                    .multiply(new BigDecimal(remainingDays), MATH_CONTEXT)
                    .divide(new BigDecimal(totalLoanDays - 30), MATH_CONTEXT);
        }

        return fee.setScale(0, RoundingMode.HALF_UP);
    }


}

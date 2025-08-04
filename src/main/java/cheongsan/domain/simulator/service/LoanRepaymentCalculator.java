package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.LoanDTO;
import cheongsan.domain.simulator.dto.MonthlyPaymentDetailDTO;
import cheongsan.domain.simulator.dto.PaymentResultDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class LoanRepaymentCalculator {

    public static final MathContext MATH_CONTEXT = new MathContext(15, RoundingMode.HALF_UP);

    public PaymentResultDTO calculateEqualPayment(LoanDTO loan) {
        // 매달 납입 정보를 담은 리스트 (상환 결과)
        List<MonthlyPaymentDetailDTO> payments = new ArrayList<>();
        // 총 납입금 (원금 + 이자 합계)
        BigDecimal totalPayment = BigDecimal.ZERO;
        BigDecimal monthlyInterestRate = loan.getMonthlyInterestRate();
        BigDecimal remainingPrincipal = loan.getPrincipal();

        BigDecimal totalInterest = BigDecimal.ZERO;

        int paymentDay = loan.getPaymentDate().getDayOfMonth();  // 예: 1일, 5일 등
        LocalDate firstPaymentDate = calculateNextPaymentDate(paymentDay, LocalDate.now());
        long remainingMonths = loan.getRemainingMonths(firstPaymentDate);
        LocalDate paymentDate = firstPaymentDate;
        BigDecimal paidPrincipal = BigDecimal.ZERO;
        if (monthlyInterestRate.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal monthlyPayment = remainingPrincipal.divide(BigDecimal.valueOf(remainingMonths), MATH_CONTEXT);
            for (int i = 1; i <= remainingMonths; i++) {
                paymentDate = paymentDate.plusMonths(1);
                paidPrincipal = paidPrincipal.add(monthlyPayment);
                remainingPrincipal = remainingPrincipal.subtract(monthlyPayment);

                payments.add(new MonthlyPaymentDetailDTO(
                        i,
                        monthlyPayment,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        monthlyPayment,
                        paymentDate,
                        remainingPrincipal.max(BigDecimal.ZERO)
                ));
            }
            totalPayment = paidPrincipal;

            return PaymentResultDTO.builder()
                    .totalPayment(totalPayment.setScale(0, RoundingMode.HALF_UP))
                    .payments(payments)
                    .totalInterest(BigDecimal.ZERO)
                    .totalPrepaymentFee(BigDecimal.ZERO)
                    .build();
        }

        BigDecimal one = BigDecimal.ONE;
        BigDecimal pow = (one.add(monthlyInterestRate)).pow((int) remainingMonths, MATH_CONTEXT);
        // 매달 고정으로 내야 할 금액 (원리금 합계)
        BigDecimal monthlyPayment = remainingPrincipal.multiply(
                monthlyInterestRate.multiply(pow),
                MATH_CONTEXT
        ).divide(
                pow.subtract(one, MATH_CONTEXT),
                MATH_CONTEXT
        );


        for (int i = 1; i <= remainingMonths; i++) {
            // 이번 달 이자 (남은 원금 * 이자율)
            BigDecimal nowInterest = remainingPrincipal.multiply(monthlyInterestRate, MATH_CONTEXT);
            // 이번 달 원금 상환분 (monthlyPayment - nowInterest)
            BigDecimal nowPrincipal = monthlyPayment.subtract(nowInterest, MATH_CONTEXT);
            // 매회 원금 상환 후 남은 원금
            remainingPrincipal = remainingPrincipal.subtract(nowPrincipal, MATH_CONTEXT);
            if (remainingPrincipal.compareTo(BigDecimal.ZERO) < 0) {
                remainingPrincipal = BigDecimal.ZERO;
            }
            totalInterest = totalInterest.add(nowInterest, MATH_CONTEXT);
            payments.add(new MonthlyPaymentDetailDTO(
                    i,
                    nowPrincipal.setScale(0, RoundingMode.HALF_UP),
                    nowInterest.setScale(0, RoundingMode.HALF_UP),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    monthlyPayment.setScale(0, RoundingMode.HALF_UP),
                    paymentDate,
                    remainingPrincipal
            ));
            paymentDate = paymentDate.plusMonths(1);

            totalPayment = totalPayment.add(monthlyPayment, MATH_CONTEXT);
        }
        return PaymentResultDTO.builder()
                .totalPayment(totalPayment.setScale(0, RoundingMode.HALF_UP))
                .payments(payments)
                .totalInterest(totalInterest.setScale(0, RoundingMode.HALF_UP))
                .totalPrepaymentFee(BigDecimal.ZERO)
                .build();

    }


    public PaymentResultDTO calculateEqualPrincipal(LoanDTO loan) {
        // 매달 갚아야 할 고정 원금 (remainingPrincipal / months)
        BigDecimal remainingPrincipal = loan.getPrincipal();
        int paymentDay = loan.getPaymentDate().getDayOfMonth();  // 예: 1일, 5일 등
        LocalDate firstPaymentDate = calculateNextPaymentDate(paymentDay, LocalDate.now());
        long remainingMonths = loan.getRemainingMonths(firstPaymentDate);

        BigDecimal totalInterest = BigDecimal.ZERO;
        // 월 납입 원금
        BigDecimal monthlyPrincipal = remainingPrincipal.divide(BigDecimal.valueOf(remainingMonths), MATH_CONTEXT);
        // 전체 납입 총액 (이자 + 원금 합계) 누적용
        BigDecimal totalPayment = BigDecimal.ZERO;
        // 해당 월의 이자
        BigDecimal nowInterest;
        BigDecimal monthlyInterestRate = loan.getMonthlyInterestRate();
        // 매달 납입 내역을 저장할 리스트
        List<MonthlyPaymentDetailDTO> payments = new ArrayList<>();
        LocalDate paymentDate = firstPaymentDate;
        for (int i = 1; i <= remainingMonths; i++) {
            nowInterest = remainingPrincipal.multiply(monthlyInterestRate);
            remainingPrincipal = remainingPrincipal.subtract(monthlyPrincipal);
            // 해당 월의 총 납입금 (고정 원금 + 이자)
            BigDecimal monthlyPayment = monthlyPrincipal.add(nowInterest, MATH_CONTEXT);
            totalPayment = totalPayment.add(monthlyPrincipal.add(nowInterest, MATH_CONTEXT));

            totalInterest = totalInterest.add(nowInterest, MATH_CONTEXT);
            payments.add(new MonthlyPaymentDetailDTO(i,
                    monthlyPrincipal.setScale(0, RoundingMode.HALF_UP),
                    nowInterest.setScale(0, RoundingMode.HALF_UP),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    monthlyPayment.setScale(0, RoundingMode.HALF_UP),
                    paymentDate,
                    remainingPrincipal
            ));
            paymentDate = paymentDate.plusMonths(1);
        }
        return PaymentResultDTO.builder()
                .totalPayment(totalPayment.setScale(0, RoundingMode.HALF_UP))
                .payments(payments)
                .totalInterest(totalInterest.setScale(0, RoundingMode.HALF_UP))
                .totalPrepaymentFee(BigDecimal.ZERO)
                .build();
    }

    public PaymentResultDTO calculateEqualPaymentWithFixedPrepayment(BigDecimal monthlyPrepayment, LoanDTO loan) {
        List<MonthlyPaymentDetailDTO> payments = new ArrayList<>();
        BigDecimal totalPayment = BigDecimal.ZERO;
        BigDecimal one = BigDecimal.ONE;

        int paymentDay = loan.getPaymentDate().getDayOfMonth();  // 예: 1일, 5일 등
        LocalDate firstPaymentDate = calculateNextPaymentDate(paymentDay, LocalDate.now());
        long remainingMonths = loan.getRemainingMonths(firstPaymentDate);
        BigDecimal monthlyInterestRate = loan.getMonthlyInterestRate();
        BigDecimal remainingPrincipal = loan.getPrincipal();

        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal totalPrepaymentFee = BigDecimal.ZERO;

        // 고정 상환금 계산 (초기 고정값)
        BigDecimal pow = (one.add(monthlyInterestRate)).pow((int) remainingMonths, MATH_CONTEXT);
        BigDecimal monthlyPayment = remainingPrincipal.multiply(
                monthlyInterestRate.multiply(pow), MATH_CONTEXT
        ).divide(
                pow.subtract(one, MATH_CONTEXT), MATH_CONTEXT
        );
        long actualMonthCount = 0;
        LocalDate paymentDate = firstPaymentDate;
        for (int i = 0; i < remainingMonths; i++) {
            if (remainingPrincipal.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal nowInterest = remainingPrincipal.multiply(monthlyInterestRate, MATH_CONTEXT);
            BigDecimal nowPrincipal = monthlyPayment.subtract(nowInterest, MATH_CONTEXT);

            // 원금보다 많이 갚지 않도록 조정
            BigDecimal actualMonthlyPrincipal = nowPrincipal.min(remainingPrincipal);

            BigDecimal actualPrepayment = monthlyPrepayment.min(
                    remainingPrincipal.subtract(actualMonthlyPrincipal, MATH_CONTEXT).max(BigDecimal.ZERO)
            );


            BigDecimal fee = BigDecimal.ZERO;
            if (actualPrepayment.compareTo(BigDecimal.ZERO) > 0) {
                fee = calculatePrepaymentFee(actualPrepayment, paymentDate, loan);
            }

            // 남은 원금 갱신
            remainingPrincipal = remainingPrincipal
                    .subtract(actualMonthlyPrincipal, MATH_CONTEXT)
                    .subtract(actualPrepayment, MATH_CONTEXT);
            if (remainingPrincipal.compareTo(BigDecimal.ZERO) < 0) {
                remainingPrincipal = BigDecimal.ZERO;
            }

            BigDecimal totalMonthlyPayment = monthlyPayment
                    .add(actualPrepayment, MATH_CONTEXT)
                    .add(fee, MATH_CONTEXT);

            totalInterest = totalInterest.add(nowInterest, MATH_CONTEXT);
            totalPrepaymentFee = totalPrepaymentFee.add(fee, MATH_CONTEXT);
            payments.add(new MonthlyPaymentDetailDTO(
                    i + 1,
                    actualMonthlyPrincipal.setScale(0, RoundingMode.HALF_UP),
                    nowInterest.setScale(0, RoundingMode.HALF_UP),
                    actualPrepayment.setScale(0, RoundingMode.HALF_UP),
                    fee,
                    totalMonthlyPayment.setScale(0, RoundingMode.HALF_UP),
                    paymentDate,
                    remainingPrincipal
            ));

            totalPayment = totalPayment.add(totalMonthlyPayment, MATH_CONTEXT);
            paymentDate = paymentDate.plusMonths(1);
        }


        return PaymentResultDTO.builder()
                .totalPayment(totalPayment.setScale(0, RoundingMode.HALF_UP))
                .payments(payments)
                .debtFreeDate(paymentDate.minusMonths(1))
                .actualMonthCount(actualMonthCount)
                .totalPrepaymentFee(totalPrepaymentFee)
                .totalInterest(totalInterest)
                .build();
    }


    public PaymentResultDTO calculateEqualPrincipalWithFixedPrepayment(BigDecimal monthlyPrepayment, LoanDTO loan) {
        List<MonthlyPaymentDetailDTO> payments = new ArrayList<>();
        BigDecimal totalPayment = BigDecimal.ZERO;

        BigDecimal remainingPrincipal = loan.getPrincipal();
        BigDecimal monthlyInterestRate = loan.getMonthlyInterestRate();
        int paymentDay = loan.getPaymentDate().getDayOfMonth();  // 예: 1일, 5일 등
        LocalDate firstPaymentDate = calculateNextPaymentDate(paymentDay, LocalDate.now());
        long remainingMonths = loan.getRemainingMonths(firstPaymentDate);
        // 고정 원금 상환액
        BigDecimal fixedMonthlyPrincipal = remainingPrincipal.divide(BigDecimal.valueOf(remainingMonths), MATH_CONTEXT);
        long actualMonthCount = 0;
        LocalDate paymentDate = firstPaymentDate;

        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal totalPrepaymentFee = BigDecimal.ZERO;
        for (int i = 0; i < remainingMonths; i++) {
            if (remainingPrincipal.compareTo(BigDecimal.ZERO) == 0) {
                break; // 원금 전액 상환 완료
            }
            // 이번 달 실제 원금 상환액 (남은 원금보다 크지 않게 조정)
            BigDecimal actualMonthlyPrincipal = fixedMonthlyPrincipal.min(remainingPrincipal);

            // 중도상환금 계산 (남은 원금에서 고정 원금 상환분을 제외한 후 중도상환금 적용)
            BigDecimal maxAvailablePrepayment = remainingPrincipal.subtract(actualMonthlyPrincipal, MATH_CONTEXT);
            BigDecimal actualPrepayment = monthlyPrepayment.min(maxAvailablePrepayment.max(BigDecimal.ZERO));

            // 이자 계산
            BigDecimal nowInterest = remainingPrincipal.multiply(monthlyInterestRate, MATH_CONTEXT);

            // 수수료 계산
            BigDecimal fee = BigDecimal.ZERO;
            if (actualPrepayment.compareTo(BigDecimal.ZERO) > 0) {
                fee = calculatePrepaymentFee(actualPrepayment, paymentDate, loan);
            }

            // 총 납부금 계산
            BigDecimal monthlyPayment = actualMonthlyPrincipal
                    .add(nowInterest, MATH_CONTEXT)
                    .add(actualPrepayment, MATH_CONTEXT)
                    .add(fee, MATH_CONTEXT);

            totalInterest = totalInterest.add(nowInterest, MATH_CONTEXT);
            totalPrepaymentFee = totalPrepaymentFee.add(fee, MATH_CONTEXT);
            // 남은 원금 차감
            remainingPrincipal = remainingPrincipal
                    .subtract(actualMonthlyPrincipal, MATH_CONTEXT)
                    .subtract(actualPrepayment, MATH_CONTEXT);

            if (remainingPrincipal.compareTo(BigDecimal.ZERO) < 0) {
                remainingPrincipal = BigDecimal.ZERO;
            }
            // 결과 추가
            payments.add(new MonthlyPaymentDetailDTO(
                    i + 1,
                    actualMonthlyPrincipal.setScale(0, RoundingMode.HALF_UP),
                    nowInterest.setScale(0, RoundingMode.HALF_UP),
                    actualPrepayment.setScale(0, RoundingMode.HALF_UP),
                    fee,
                    monthlyPayment.setScale(0, RoundingMode.HALF_UP),
                    paymentDate,
                    remainingPrincipal
            ));


            paymentDate = paymentDate.plusMonths(1);
            totalPayment = totalPayment.add(monthlyPayment, MATH_CONTEXT);
            actualMonthCount++;
        }


        return PaymentResultDTO.builder()
                .totalPayment(totalPayment.setScale(0, RoundingMode.HALF_UP))
                .payments(payments)
                .debtFreeDate(paymentDate)
                .actualMonthCount(actualMonthCount)
                .totalInterest(totalInterest)
                .totalPrepaymentFee(totalPrepaymentFee)
                .build();
    }

    public PaymentResultDTO calculateLumpSumRepayment(LoanDTO loan) {
        BigDecimal monthlyRate = loan.getMonthlyInterestRate();
        List<MonthlyPaymentDetailDTO> payments = new ArrayList<>();
        BigDecimal totalPayment = BigDecimal.ZERO;

        int paymentDay = loan.getPaymentDate().getDayOfMonth();  // 예: 1일, 5일 등
        LocalDate firstPaymentDate = calculateNextPaymentDate(paymentDay, LocalDate.now());
        long remainingMonths = loan.getRemainingMonths(firstPaymentDate);
        LocalDate paymentDate = firstPaymentDate;
        BigDecimal totalInterest = BigDecimal.ZERO;
        for (int i = 0; i < remainingMonths; i++) {
            BigDecimal interest = loan.getPrincipal().multiply(monthlyRate, MATH_CONTEXT);
            BigDecimal principalPayment = BigDecimal.ZERO;
            BigDecimal totalMonthlyPayment = interest;


            if (i == remainingMonths - 1) {
                principalPayment = loan.getPrincipal();
                totalMonthlyPayment = totalMonthlyPayment.add(loan.getPrincipal(), MATH_CONTEXT);
            }

            totalInterest = totalInterest.add(interest, MATH_CONTEXT);
            payments.add(new MonthlyPaymentDetailDTO(
                    i + 1,
                    principalPayment.setScale(0, RoundingMode.HALF_UP),
                    interest.setScale(0, RoundingMode.HALF_UP),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    totalMonthlyPayment.setScale(0, RoundingMode.HALF_UP),
                    paymentDate,
                    loan.getPrincipal()
            ));

            paymentDate = paymentDate.plusMonths(1);
            totalPayment = totalPayment.add(totalMonthlyPayment);
        }
        return PaymentResultDTO.builder()
                .totalPayment(totalPayment.setScale(0, RoundingMode.HALF_UP))
                .totalInterest(totalInterest.setScale(0, RoundingMode.HALF_UP))
                .totalPrepaymentFee(BigDecimal.ZERO)
                .payments(payments)
                .build();
    }

    public PaymentResultDTO calculateLumpSumRepaymentWithPrepayment(BigDecimal monthlyPrepayment, LoanDTO loan) {
        BigDecimal monthlyInterestRate = loan.getMonthlyInterestRate();
        List<MonthlyPaymentDetailDTO> payments = new ArrayList<>();
        BigDecimal totalPayment = BigDecimal.ZERO;
        BigDecimal remainingPrincipal = loan.getPrincipal();

        long actualMonthCount = 0;

        int paymentDay = loan.getPaymentDate().getDayOfMonth();  // 예: 1일, 5일 등
        LocalDate firstPaymentDate = calculateNextPaymentDate(paymentDay, LocalDate.now());
        long remainingMonths = loan.getRemainingMonths(firstPaymentDate);
        LocalDate paymentDate = firstPaymentDate;
        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal totalPrepaymentFee = BigDecimal.ZERO;

        for (int i = 0; i < remainingMonths; i++) {
            // 남은 원금 기준으로 매월 이자 계산
            BigDecimal nowInterest = remainingPrincipal.multiply(monthlyInterestRate, MATH_CONTEXT);
            BigDecimal principalPayment = BigDecimal.ZERO;


            // 중도상환 (조기상환)
            if (monthlyPrepayment.compareTo(BigDecimal.ZERO) > 0) {
                if (monthlyPrepayment.compareTo(remainingPrincipal) >= 0) {
                    principalPayment = remainingPrincipal;
                    remainingPrincipal = BigDecimal.ZERO;
                } else {
                    principalPayment = monthlyPrepayment;
                    remainingPrincipal = remainingPrincipal.subtract(monthlyPrepayment, MATH_CONTEXT);
                }
            }

            BigDecimal totalMonthlyPayment = nowInterest.add(principalPayment, MATH_CONTEXT);

            // 만기인 경우 남은 원금을 한꺼번에 갚음
            if (i == remainingMonths - 1 && remainingPrincipal.compareTo(BigDecimal.ZERO) > 0) {
                totalMonthlyPayment = totalMonthlyPayment.add(remainingPrincipal, MATH_CONTEXT);
                principalPayment = principalPayment.add(remainingPrincipal, MATH_CONTEXT);
                remainingPrincipal = BigDecimal.ZERO;
            }


            // 수수료 계산
            BigDecimal fee = BigDecimal.ZERO;
            if (principalPayment.compareTo(BigDecimal.ZERO) > 0) {
                fee = calculatePrepaymentFee(principalPayment, paymentDate, loan);
            }

            totalInterest = totalInterest.add(nowInterest, MATH_CONTEXT);
            totalPrepaymentFee = totalPrepaymentFee.add(fee, MATH_CONTEXT);


            payments.add(new MonthlyPaymentDetailDTO(
                    i + 1,
                    principalPayment.setScale(0, RoundingMode.HALF_UP),
                    nowInterest.setScale(0, RoundingMode.HALF_UP),
                    monthlyPrepayment.setScale(0, RoundingMode.HALF_UP),
                    fee,
                    totalMonthlyPayment.setScale(0, RoundingMode.HALF_UP),
                    paymentDate,
                    remainingPrincipal
            ));
            paymentDate = paymentDate.plusMonths(1);

            totalPayment = totalPayment.add(totalMonthlyPayment);
            actualMonthCount++;
        }
        return PaymentResultDTO.builder()
                .totalPayment(totalPayment.setScale(0, RoundingMode.HALF_UP))
                .payments(payments)
                .debtFreeDate(paymentDate.minusMonths(1))
                .actualMonthCount(actualMonthCount)
                .totalInterest(totalInterest.setScale(0, RoundingMode.HALF_UP))
                .totalPrepaymentFee(totalPrepaymentFee.setScale(0, RoundingMode.HALF_UP))
                .build();
    }

    private LocalDate calculateNextPaymentDate(int paymentDay, LocalDate today) {
        // 이번 달 상환일
        LocalDate scheduledThisMonth = today.withDayOfMonth(Math.min(paymentDay, today.lengthOfMonth()));

        // 오늘이 상환일을 지났으면 다음 달로
        if (!today.isBefore(scheduledThisMonth)) {
            LocalDate nextMonth = today.plusMonths(1);
            return nextMonth.withDayOfMonth(Math.min(paymentDay, nextMonth.lengthOfMonth()));
        }
        return scheduledThisMonth;
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

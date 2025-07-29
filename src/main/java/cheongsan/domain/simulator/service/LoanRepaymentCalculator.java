package cheongsan.domain.simulator.service;

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

    /**
     * 원리금 균등 상환
     *
     * @param interestRate       월 이자율 (예: 연 6%면 월 0.005 = 0.5%)
     * @param months             남은 상환 개월 수 (예: 12개월)
     * @param remainingPrincipal 현재 남은 원금
     * @return {@link PaymentResultDTO} 총 납입금과 각 월별 납입 상세 정보 리스트 포함
     */
    public PaymentResultDTO calculateEqualPayment(BigDecimal interestRate, int months, BigDecimal remainingPrincipal) {
        // 매달 납입 정보를 담은 리스트 (상환 결과)
        List<MonthlyPaymentDetailDTO> payments = new ArrayList<>();
        // 총 납입금 (원금 + 이자 합계)
        BigDecimal totalPayment = BigDecimal.ZERO;

        if (interestRate.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal monthlyPayment = remainingPrincipal.divide(BigDecimal.valueOf(months), MATH_CONTEXT);
            for (int i = 1; i <= months; i++) {
                payments.add(new MonthlyPaymentDetailDTO(i, monthlyPayment, BigDecimal.ZERO, monthlyPayment));
            }
            totalPayment = remainingPrincipal;
            return new PaymentResultDTO(totalPayment.setScale(0, RoundingMode.HALF_UP), payments);
        }

        BigDecimal one = BigDecimal.ONE;
        BigDecimal pow = (one.add(interestRate)).pow(months, MATH_CONTEXT);
        // 매달 고정으로 내야 할 금액 (원리금 합계)
        BigDecimal monthlyPayment = remainingPrincipal.multiply(
                interestRate.multiply(pow),
                MATH_CONTEXT
        ).divide(
                pow.subtract(one, MATH_CONTEXT),
                MATH_CONTEXT
        );

        for (int i = 1; i <= months; i++) {
            // 이번 달 이자 (남은 원금 * 이자율)
            BigDecimal nowInterest = remainingPrincipal.multiply(interestRate, MATH_CONTEXT);
            // 이번 달 원금 상환분 (monthlyPayment - nowInterest)
            BigDecimal nowPrincipal = monthlyPayment.subtract(nowInterest, MATH_CONTEXT);
            // 매회 원금 상환 후 남은 원금
            remainingPrincipal = remainingPrincipal.subtract(nowPrincipal, MATH_CONTEXT);
            if (remainingPrincipal.compareTo(BigDecimal.ZERO) < 0) {
                remainingPrincipal = BigDecimal.ZERO;
            }

            payments.add(new MonthlyPaymentDetailDTO(
                    i,
                    nowPrincipal.setScale(0, RoundingMode.HALF_UP),
                    nowInterest.setScale(0, RoundingMode.HALF_UP),
                    monthlyPayment.setScale(0, RoundingMode.HALF_UP)
            ));

            totalPayment = totalPayment.add(monthlyPayment, MATH_CONTEXT);
        }
        return new PaymentResultDTO(totalPayment.setScale(0, RoundingMode.HALF_UP), payments);

    }

    /**
     * 원금 균등 상환
     *
     * @param interestRate       월 이자율 (예: 연 6%면 월 0.005)
     * @param months             총 상환 개월 수
     * @param remainingPrincipal 현재 남은 원금
     * @return {@link PaymentResultDTO} 총 납입금과 각 월별 납입 상세 정보 리스트 포함
     */
    public PaymentResultDTO calculateEqualPrincipal(BigDecimal interestRate, long months, BigDecimal remainingPrincipal) {
        // 매달 갚아야 할 고정 원금 (remainingPrincipal / months)
        BigDecimal monthlyPrincipal = remainingPrincipal.divide(BigDecimal.valueOf(months), MATH_CONTEXT);
        // 전체 납입 총액 (이자 + 원금 합계) 누적용
        BigDecimal totalPayment = BigDecimal.ZERO;
        // 상환 전 잔액
        BigDecimal principalBeforePayment;
        // 해당 월의 이자
        BigDecimal nowInterest;
        // 매달 납입 내역을 저장할 리스트
        List<MonthlyPaymentDetailDTO> payments = new ArrayList<>();
        for (int i = 1; i <= months; i++) {
            principalBeforePayment = remainingPrincipal.subtract(monthlyPrincipal.multiply(BigDecimal.valueOf(i - 1), MATH_CONTEXT));
            nowInterest = principalBeforePayment.multiply(interestRate);
            // 해당 월의 총 납입금 (고정 원금 + 이자)
            BigDecimal monthlyPayment = monthlyPrincipal.add(nowInterest, MATH_CONTEXT);
            totalPayment = totalPayment.add(monthlyPrincipal.add(nowInterest, MATH_CONTEXT));
            payments.add(new MonthlyPaymentDetailDTO(i,
                    monthlyPrincipal.setScale(0, RoundingMode.HALF_UP),
                    nowInterest.setScale(0, RoundingMode.HALF_UP),
                    monthlyPayment.setScale(0, RoundingMode.HALF_UP)));
        }
        return new PaymentResultDTO(totalPayment, payments);
    }

    /**
     * 원리금 균등 상환 방식 + 고정 중도상환금 + 수수료 반영
     *
     * @param interestRate       월 이자율 (ex 0.02 = 2%)
     * @param months             전체 상환 기간 (개월 수)
     * @param remainingPrincipal 남은 원금
     * @param monthlyPrepayment  매월 중도상환할 금액
     * @param prepaymentFeeRate  중도상환 수수료율 (ex 0.01 = 1%)
     * @param loanStartDate      대출 시작일
     * @param loanEndDate        대출 종료일
     * @return {@link PaymentResultDTO} 총 납입금과 각 월별 납입 상세 정보 리스트 포함
     */
    public PaymentResultDTO calculateEqualPaymentWithFixedPrepayment(
            BigDecimal interestRate,
            int months,
            BigDecimal remainingPrincipal,
            BigDecimal monthlyPrepayment,
            BigDecimal prepaymentFeeRate,
            LocalDate loanStartDate,
            LocalDate loanEndDate
    ) {
        List<MonthlyPaymentDetailDTO> payments = new ArrayList<>();
        BigDecimal totalPayment = BigDecimal.ZERO;
        BigDecimal one = BigDecimal.ONE;

        // 고정 상환금 계산 (초기 고정값)
        BigDecimal pow = (one.add(interestRate)).pow(months, MATH_CONTEXT);
        BigDecimal monthlyPayment = remainingPrincipal.multiply(
                interestRate.multiply(pow), MATH_CONTEXT
        ).divide(
                pow.subtract(one, MATH_CONTEXT), MATH_CONTEXT
        );

        for (int i = 0; i < months; i++) {
            if (remainingPrincipal.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal nowInterest = remainingPrincipal.multiply(interestRate, MATH_CONTEXT);
            BigDecimal nowPrincipal = monthlyPayment.subtract(nowInterest, MATH_CONTEXT);

            // 원금보다 많이 갚지 않도록 조정
            BigDecimal actualMonthlyPrincipal = nowPrincipal.min(remainingPrincipal);

            BigDecimal actualPrepayment = monthlyPrepayment.min(
                    remainingPrincipal.subtract(actualMonthlyPrincipal, MATH_CONTEXT).max(BigDecimal.ZERO)
            );

            LocalDate currentDate = loanStartDate.plusMonths(i);
            BigDecimal fee = BigDecimal.ZERO;
            if (actualPrepayment.compareTo(BigDecimal.ZERO) > 0) {
                fee = calculatePrepaymentFee(actualPrepayment, prepaymentFeeRate, currentDate, loanEndDate, loanStartDate);
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

            payments.add(new MonthlyPaymentDetailDTO(
                    i + 1,
                    actualMonthlyPrincipal.setScale(0, RoundingMode.HALF_UP),
                    nowInterest.setScale(0, RoundingMode.HALF_UP),
                    totalMonthlyPayment.setScale(0, RoundingMode.HALF_UP)
            ));

            totalPayment = totalPayment.add(totalMonthlyPayment, MATH_CONTEXT);
        }

        return new PaymentResultDTO(
                totalPayment.setScale(0, RoundingMode.HALF_UP),
                payments
        );
    }


    /**
     * 원금 균등 상환 방식 + 고정 중도상환금
     *
     * @param interestRate       월 이자율
     * @param months             총 상환 개월 수
     * @param remainingPrincipal 초기 대출 원금
     * @param monthlyPrepayment  매월 고정 중도상환 금액
     * @param prepaymentFeeRate  중도상환 수수료율 (ex 0.01 = 1%)
     * @return {@link PaymentResultDTO} 총 납입금과 각 월별 납입 상세 정보 리스트 포함
     */
    public PaymentResultDTO calculateEqualPrincipalWithFixedPrepayment(
            BigDecimal interestRate,
            long months,
            BigDecimal remainingPrincipal,
            BigDecimal monthlyPrepayment,
            BigDecimal prepaymentFeeRate,
            LocalDate loanStartDate,
            LocalDate loanEndDate
    ) {
        List<MonthlyPaymentDetailDTO> payments = new ArrayList<>();
        BigDecimal totalPayment = BigDecimal.ZERO;


        // 고정 원금 상환액
        BigDecimal fixedMonthlyPrincipal = remainingPrincipal.divide(BigDecimal.valueOf(months), MATH_CONTEXT);

        for (int i = 0; i < months; i++) {
            if (remainingPrincipal.compareTo(BigDecimal.ZERO) == 0) {
                break; // 원금 전액 상환 완료
            }

            LocalDate currentDate = loanStartDate.plusMonths(i);

            // 이번 달 실제 원금 상환액 (남은 원금보다 크지 않게 조정)
            BigDecimal actualMonthlyPrincipal = fixedMonthlyPrincipal.min(remainingPrincipal);

            // 중도상환금 계산 (남은 원금에서 고정 원금 상환분을 제외한 후 중도상환금 적용)
            BigDecimal maxAvailablePrepayment = remainingPrincipal.subtract(actualMonthlyPrincipal, MATH_CONTEXT);
            BigDecimal actualPrepayment = monthlyPrepayment.min(maxAvailablePrepayment.max(BigDecimal.ZERO));

            // 이자 계산
            BigDecimal nowInterest = remainingPrincipal.multiply(interestRate, MATH_CONTEXT);

            // 수수료 계산
            BigDecimal fee = BigDecimal.ZERO;
            if (actualPrepayment.compareTo(BigDecimal.ZERO) > 0) {
                fee = calculatePrepaymentFee(actualPrepayment, prepaymentFeeRate, currentDate, loanEndDate, loanStartDate);
            }

            // 총 납부금 계산
            BigDecimal monthlyPayment = actualMonthlyPrincipal
                    .add(nowInterest, MATH_CONTEXT)
                    .add(actualPrepayment, MATH_CONTEXT)
                    .add(fee, MATH_CONTEXT);

            // 결과 추가
            payments.add(new MonthlyPaymentDetailDTO(
                    i + 1,
                    actualMonthlyPrincipal.setScale(0, RoundingMode.HALF_UP),
                    nowInterest.setScale(0, RoundingMode.HALF_UP),
                    monthlyPayment.setScale(0, RoundingMode.HALF_UP)
            ));

            // 남은 원금 차감
            remainingPrincipal = remainingPrincipal
                    .subtract(actualMonthlyPrincipal, MATH_CONTEXT)
                    .subtract(actualPrepayment, MATH_CONTEXT);
            if (remainingPrincipal.compareTo(BigDecimal.ZERO) < 0) {
                remainingPrincipal = BigDecimal.ZERO;
            }

            totalPayment = totalPayment.add(monthlyPayment, MATH_CONTEXT);
        }

        return new PaymentResultDTO(
                totalPayment.setScale(0, RoundingMode.HALF_UP),
                payments
        );
    }


    /**
     * 중도상환 수수료 계산
     *
     * @param prepaymentPrincipal 중도상환할 금액
     * @param feeRate             수수료율 (ex 0.01 = 1%)
     * @param repaymentDate       중도상환일
     * @param loanEndDate         대출 만기일
     * @param loanStartDate       대출 시작일
     * @return 계산된 중도상환 수수료 (원 단위 반올림)
     */
    public BigDecimal calculatePrepaymentFee(
            BigDecimal prepaymentPrincipal,
            BigDecimal feeRate,
            LocalDate repaymentDate,
            LocalDate loanEndDate,
            LocalDate loanStartDate
    ) {
        long remainingDays = ChronoUnit.DAYS.between(repaymentDate, loanEndDate);
        long totalLoanDays = ChronoUnit.DAYS.between(loanStartDate, loanEndDate);

        if (remainingDays <= 0 || totalLoanDays <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal proportion = new BigDecimal(remainingDays)
                .divide(new BigDecimal(totalLoanDays), MATH_CONTEXT);

        BigDecimal fee = prepaymentPrincipal
                .multiply(feeRate, MATH_CONTEXT)
                .multiply(proportion, MATH_CONTEXT);

        return fee.setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 만기 일시 상환
     *
     * @param principal        대출 원금
     * @param annualRate       연 이자율 (ex 0.06 == 6%)
     * @param loanPeriodMonths 대출 기간 (개월)
     * @param loanStartDate    대출 시작일
     * @return {@link PaymentResultDTO} 총 납입금과 각 월별 납입 상세 정보 리스트 포함
     */
    public PaymentResultDTO calculateLumpSumRepayment(BigDecimal principal, BigDecimal annualRate, int loanPeriodMonths, LocalDate loanStartDate) {
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), MATH_CONTEXT);
        List<MonthlyPaymentDetailDTO> payments = new ArrayList<>();
        BigDecimal totalPayment = BigDecimal.ZERO;

        for (int i = 0; i < loanPeriodMonths; i++) {
            BigDecimal interest = principal.multiply(monthlyRate, MATH_CONTEXT);
            BigDecimal principalPayment = BigDecimal.ZERO;
            BigDecimal totalMonthlyPayment = interest;

            if (i == loanPeriodMonths - 1) {
                principalPayment = principal;
                totalMonthlyPayment = totalMonthlyPayment.add(principal, MATH_CONTEXT);
            }

            payments.add(new MonthlyPaymentDetailDTO(
                    i + 1,
                    principalPayment.setScale(0, RoundingMode.HALF_UP),
                    interest.setScale(0, RoundingMode.HALF_UP),
                    totalMonthlyPayment.setScale(0, RoundingMode.HALF_UP)
            ));

            totalPayment = totalPayment.add(totalMonthlyPayment);
        }
        return new PaymentResultDTO(totalPayment.setScale(0, RoundingMode.HALF_UP), payments);
    }

    /**
     * 만기 일시 상환 + 중도 상환
     *
     * @param principal         대출 원금
     * @param annualRate        연이자율
     * @param loanPeriodMonths  대출 기간 (개월 수)
     * @param monthlyPrepayment 매월 중도상환 금액
     * @return {@link PaymentResultDTO} 총 납입금과 각 월별 납입 상세 정보 리스트 포함
     */
    public PaymentResultDTO calculateLumpSumRepaymentWithPrepayment(
            BigDecimal principal,
            BigDecimal annualRate,
            int loanPeriodMonths,
            BigDecimal monthlyPrepayment) {
        // 연이율 -> 월이율로 반환 (MATH_CONTEXT : 게산 정밀도 설정)
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), MATH_CONTEXT);
        List<MonthlyPaymentDetailDTO> payments = new ArrayList<>();
        BigDecimal totalPayment = BigDecimal.ZERO;
        BigDecimal remainingPrincipal = principal;

        for (int i = 0; i < loanPeriodMonths; i++) {
            // 남은 원금 기준으로 매월 이자 계산
            BigDecimal interest = remainingPrincipal.multiply(monthlyRate, MATH_CONTEXT);
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
            BigDecimal totalMonthlyPayment = interest.add(principalPayment, MATH_CONTEXT);

            // 만기인 경우 남은 원금을 한꺼번에 갚음
            if (i == loanPeriodMonths - 1 && remainingPrincipal.compareTo(BigDecimal.ZERO) > 0) {
                totalMonthlyPayment = totalMonthlyPayment.add(remainingPrincipal, MATH_CONTEXT);
                principalPayment = principalPayment.add(remainingPrincipal, MATH_CONTEXT);
                remainingPrincipal = BigDecimal.ZERO;
            }

            payments.add(new MonthlyPaymentDetailDTO(
                    i + 1,
                    principalPayment.setScale(0, RoundingMode.HALF_UP),
                    interest.setScale(0, RoundingMode.HALF_UP),
                    totalMonthlyPayment.setScale(0, RoundingMode.HALF_UP)
            ));

            totalPayment = totalPayment.add(totalMonthlyPayment);
        }
        return new PaymentResultDTO(totalPayment.setScale(0, RoundingMode.HALF_UP), payments);
    }
}

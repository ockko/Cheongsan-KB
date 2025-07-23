package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.MonthlyPaymentDetailDTO;
import cheongsan.domain.simulator.dto.RepaymentResultDTO;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Calculator {

    private static final MathContext MATH_CONTEXT = new MathContext(15, RoundingMode.HALF_UP);

    /**
     * @param interestRate       월 이자율 (예: 연 6%면 월 0.005 = 0.5%)
     * @param months             남은 상환 개월 수 (예: 12개월)
     * @param remainingPrincipal 현재 남은 원금
     * @return {@link RepaymentResultDTO} 총 납입금과 각 월별 납입 상세 정보 리스트 포함
     */
    public RepaymentResultDTO calculateEqualPayment(BigDecimal interestRate, BigDecimal months, BigDecimal remainingPrincipal) {
        // 매달 납입 정보를 담은 리스트 (상환 결과)
        List<MonthlyPaymentDetailDTO> payments = new ArrayList<>();
        // 총 납입금 (원금 + 이자 합계)
        BigDecimal totalPayment = BigDecimal.ZERO;

        if (interestRate.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal monthlyPayment = remainingPrincipal.divide(months, MATH_CONTEXT);
            for (int i = 1; i <= months.intValueExact(); i++) {
                payments.add(new MonthlyPaymentDetailDTO(i, monthlyPayment, BigDecimal.ZERO, monthlyPayment));
            }
            totalPayment = remainingPrincipal;
            return new RepaymentResultDTO(totalPayment.setScale(0, RoundingMode.HALF_UP), payments);
        }

        BigDecimal one = BigDecimal.ONE;
        BigDecimal pow = (one.add(interestRate)).pow(months.intValueExact(), MATH_CONTEXT);
        // 매달 고정으로 내야 할 금액 (원리금 합계)
        BigDecimal monthlyPayment = remainingPrincipal.multiply(
                interestRate.multiply(pow),
                MATH_CONTEXT
        ).divide(
                pow.subtract(one, MATH_CONTEXT),
                MATH_CONTEXT
        );

        for (int i = 1; i <= months.intValueExact(); i++) {
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
        return new RepaymentResultDTO(totalPayment.setScale(0, RoundingMode.HALF_UP), payments);

    }

    /**
     * @param interestRate       월 이자율 (예: 연 6%면 월 0.005)
     * @param months             총 상환 개월 수
     * @param remainingPrincipal 현재 남은 원금
     * @return {@link RepaymentResultDTO} 총 납입금과 각 월별 납입 상세 정보 리스트 포함
     */
    public RepaymentResultDTO calculateEqualPrincipal(BigDecimal interestRate, BigDecimal months, BigDecimal remainingPrincipal) {
        // 매달 갚아야 할 고정 원금 (remainingPrincipal / months)
        BigDecimal monthlyPrincipal = remainingPrincipal.divide(months, MATH_CONTEXT);
        // 전체 납입 총액 (이자 + 원금 합계) 누적용
        BigDecimal totalPayment = BigDecimal.ZERO;
        // 상환 전 잔액
        BigDecimal principalBeforePayment;
        // 해당 월의 이자
        BigDecimal nowInterest;
        // 매달 납입 내역을 저장할 리스트
        List<MonthlyPaymentDetailDTO> payments = new ArrayList<>();
        for (int i = 1; i <= months.intValueExact(); i++) {
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
        return new RepaymentResultDTO(totalPayment, payments);
    }
}

package cheongsan.common.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class BudgetCalculator {
    private static final BigDecimal RECOMMENDATION_RATE = new BigDecimal("0.7");
    private static final int DAYS_IN_MONTH = 30;
    private static final BigDecimal ROUNDING_UNIT = new BigDecimal("500");

    public BigDecimal calculateAvailableMonthlySpending(
            BigDecimal monthlyIncome,
            BigDecimal totalMonthlyDebtPayment,
            BigDecimal monthlyFixedExpenses) {
        BigDecimal totalFixedCosts = totalMonthlyDebtPayment.add(monthlyFixedExpenses);
        BigDecimal availableSpending = monthlyIncome.subtract(totalFixedCosts);
        return availableSpending.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : availableSpending;
    }

    public int calculateMaximumLimit(BigDecimal availableMonthlySpending) {
        BigDecimal dailyAvailableAmount = availableMonthlySpending.divide(
                new BigDecimal(DAYS_IN_MONTH), 0, RoundingMode.DOWN
        );
        return roundDownToUnit(dailyAvailableAmount);
    }

    public int calculateRecommendedLimit(int maximumLimit) {
        BigDecimal recommendedLimit = new BigDecimal(maximumLimit)
                .multiply(RECOMMENDATION_RATE)
                .setScale(0, RoundingMode.DOWN);
        return roundDownToUnit(recommendedLimit);
    }

    private int roundDownToUnit(BigDecimal value) {
        if (ROUNDING_UNIT.compareTo(BigDecimal.ZERO) == 0) {
            return value.intValue();
        }
        return value.divide(ROUNDING_UNIT, 0, RoundingMode.DOWN)
                .multiply(ROUNDING_UNIT)
                .intValue();
    }
}

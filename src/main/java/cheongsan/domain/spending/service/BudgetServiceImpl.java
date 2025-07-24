package cheongsan.domain.spending.service;

import cheongsan.domain.debt.service.DebtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Log4j2
@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final TransactionService transactionService;
    private final DebtService debtService;

    private static final BigDecimal SPENDING_LIMIT_RATE = new BigDecimal("0.7");
    private static final int DAYS_IN_MONTH = 30;

    @Override
    public int calculateRecommendedDailyLimit(Long userId) {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        BigDecimal monthlyTransfer = transactionService.calculateRegularMonthlyTransfer(
                userId,
                lastMonth.getYear(),
                lastMonth.getMonthValue()
        );

        if (monthlyTransfer == null || monthlyTransfer.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("추천 한도를 계산하기 위해 월 소득 정보가 필요합니다.");
        }

        BigDecimal totalMonthlyDebtPayment = debtService.calculateTotalMonthlyPayment(userId);

        BigDecimal monthlyFixedWithdraw = transactionService.calculateMonthlyFixedWithdraw(
                userId,
                lastMonth.getYear(),
                lastMonth.getMonthValue()
        );

        BigDecimal totalFixedCosts = totalMonthlyDebtPayment.add(monthlyFixedWithdraw);

        BigDecimal availableMonthlySpending = monthlyTransfer.subtract(totalFixedCosts);

        if (availableMonthlySpending.compareTo(BigDecimal.ZERO) < 0) {
            availableMonthlySpending = BigDecimal.ZERO;
        }

        BigDecimal dailyAvailableAmount = availableMonthlySpending.divide(
                new BigDecimal(DAYS_IN_MONTH), 0, RoundingMode.DOWN
        );

        BigDecimal recommendedDailyLimit = dailyAvailableAmount
                .multiply(SPENDING_LIMIT_RATE)
                .setScale(0, RoundingMode.DOWN);

        return recommendedDailyLimit.intValue();
    }
}

package cheongsan.domain.deposit.service;

import cheongsan.common.constant.ResponseMessage;
import cheongsan.common.util.BudgetCalculator;
import cheongsan.domain.debt.service.DebtService;
import cheongsan.domain.deposit.dto.BudgetLimitDTO;
import cheongsan.domain.deposit.dto.BudgetSettingStatusDTO;
import cheongsan.domain.deposit.dto.FinancialSummaryDTO;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Log4j2
@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final DepositService depositService;
    private final DebtService debtService;
    private final UserMapper userMapper;
    private final BudgetCalculator budgetCalculator;

    @Override
    public BudgetLimitDTO getBudgetLimits(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException(ResponseMessage.USER_NOT_FOUND.getMessage());
        }
        int currentDailyLimit = (user.getDailyLimit() != null) ? user.getDailyLimit().intValue() : 0;

        FinancialSummaryDTO summary = getFinancialSummary(userId);

        BigDecimal availableSpending = budgetCalculator.calculateAvailableMonthlySpending(
                summary.getMonthlyIncome(), summary.getTotalDebtPayment(), summary.getFixedExpense()
        );
        int maximumLimit = budgetCalculator.calculateMaximumLimit(availableSpending);
        int recommendedLimit = budgetCalculator.calculateRecommendedLimit(maximumLimit);

        return new BudgetLimitDTO(recommendedLimit, maximumLimit, currentDailyLimit);
    }

    @Override
    public void saveFinalDailyLimit(Long userId, int finalDailyLimit) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException(ResponseMessage.USER_NOT_FOUND.getMessage());
        }

        checkIfUpdateIsAllowed(user.getDailyLimitDate());

        FinancialSummaryDTO summary = getFinancialSummary(userId);

        BigDecimal availableSpending = budgetCalculator.calculateAvailableMonthlySpending(summary.getMonthlyIncome(), summary.getTotalDebtPayment(), summary.getFixedExpense());
        int maximumLimit = budgetCalculator.calculateMaximumLimit(availableSpending);

        if (finalDailyLimit > maximumLimit) {
            throw new IllegalArgumentException(ResponseMessage.BUDGET_LIMIT_EXCEEDED.getMessage());
        }

        userMapper.updateDailyLimitAndTimestamp(userId, new BigDecimal(finalDailyLimit), LocalDateTime.now());
    }

    @Override
    public BudgetSettingStatusDTO getBudgetSettingStatus(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException(ResponseMessage.USER_NOT_FOUND.getMessage());
        }

        return new BudgetSettingStatusDTO(user.getDailyLimitDate());
    }

    private FinancialSummaryDTO getFinancialSummary(Long userId) {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        BigDecimal monthlyIncome = depositService.calculateRegularMonthlyTransfer(
                userId,
                lastMonth.getYear(),
                lastMonth.getMonthValue()
        );

        if (monthlyIncome == null || monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException(ResponseMessage.INCOME_NOT_FOUND_FOR_BUDGET_RECOMMENDATION.getMessage());
        }

        BigDecimal totalMonthlyDebtPayment = debtService.calculateTotalMonthlyPayment(userId);
        BigDecimal monthlyFixedWithdraw = depositService.calculateMonthlyFixedWithdraw(
                userId,
                lastMonth.getYear(),
                lastMonth.getMonthValue()
        );

        return new FinancialSummaryDTO(monthlyIncome, totalMonthlyDebtPayment, monthlyFixedWithdraw);
    }

    private void checkIfUpdateIsAllowed(LocalDateTime lastDailyLimitDate) {
        if (lastDailyLimitDate == null) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate().atStartOfDay();
        LocalDateTime endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).toLocalDate().atTime(23, 59, 59);

        if (!lastDailyLimitDate.isBefore(startOfWeek) && !lastDailyLimitDate.isAfter(endOfWeek)) {
            throw new IllegalStateException(ResponseMessage.BUDGET_UPDATE_RULE_VIOLATED.getMessage());
        }
    }
}

package cheongsan.domain.deposit.service;

import cheongsan.common.constant.ResponseMessage;
import cheongsan.domain.debt.service.DebtService;
import cheongsan.domain.deposit.dto.BudgetLimitDTO;
import cheongsan.domain.deposit.dto.BudgetSettingStatusDTO;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    private static final BigDecimal RECOMMENDATION_RATE = new BigDecimal("0.7");
    private static final int DAYS_IN_MONTH = 30;
    private static final BigDecimal ROUNDING_UNIT = new BigDecimal("500");

    @Override
    public BudgetLimitDTO getBudgetLimits(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException(ResponseMessage.USER_NOT_FOUND.getMessage());
        }
        int currentDailyLimit = (user.getDailyLimit() != null) ? user.getDailyLimit().intValue() : 0;

        BigDecimal availableMonthlySpending = calculateAvailableMonthlySpending(userId);

        BigDecimal maximumDailyLimit = availableMonthlySpending.divide(
                new BigDecimal(DAYS_IN_MONTH), 0, RoundingMode.DOWN
        );

        BigDecimal recommendedDailyLimit = maximumDailyLimit
                .multiply(RECOMMENDATION_RATE)
                .setScale(0, RoundingMode.DOWN);

        // --- 계산된 값들을 500원 단위로 내림 처리 ---
        int roundedMaximumLimit = roundDownToUnit(maximumDailyLimit, ROUNDING_UNIT);
        int roundedRecommendedLimit = roundDownToUnit(recommendedDailyLimit, ROUNDING_UNIT);

        return new BudgetLimitDTO(
                roundedRecommendedLimit,
                roundedMaximumLimit,
                currentDailyLimit
        );
    }

    @Override
    public void saveFinalDailyLimit(Long userId, int finalDailyLimit) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException(ResponseMessage.USER_NOT_FOUND.getMessage());
        }

        checkIfUpdateIsAllowed(user.getDailyLimitDate());

        BigDecimal maximumDailyLimit = calculateAvailableMonthlySpending(userId)
                .divide(new BigDecimal(DAYS_IN_MONTH), 0, RoundingMode.DOWN);

        if (new BigDecimal(finalDailyLimit).compareTo(maximumDailyLimit) > 0) {
            throw new IllegalArgumentException(ResponseMessage.BUDGET_LIMIT_EXCEEDED.getMessage());
        }

        userMapper.updateDailyLimit(userId, new BigDecimal(finalDailyLimit), LocalDateTime.now());
    }

    @Override
    public BudgetSettingStatusDTO getBudgetSettingStatus(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException(ResponseMessage.USER_NOT_FOUND.getMessage());
        }

        return new BudgetSettingStatusDTO(user.getDailyLimitDate());
    }

    // 특정 단위로 숫자를 내림(버림) 처리하는 헬퍼 메소드
    private int roundDownToUnit(BigDecimal value, BigDecimal unit) {
        if (unit.compareTo(BigDecimal.ZERO) == 0) return value.intValue();
        return value.divide(unit, 0, RoundingMode.DOWN).multiply(unit).intValue();
    }

    private BigDecimal calculateAvailableMonthlySpending(Long userId) {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        BigDecimal monthlyTransfer = depositService.calculateRegularMonthlyTransfer(
                userId,
                lastMonth.getYear(),
                lastMonth.getMonthValue()
        );

        if (monthlyTransfer == null || monthlyTransfer.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException(ResponseMessage.INCOME_NOT_FOUND_FOR_BUDGET_RECOMMENDATION.getMessage());
        }

        BigDecimal totalMonthlyDebtPayment = debtService.calculateTotalMonthlyPayment(userId);
        BigDecimal monthlyFixedWithdraw = depositService.calculateMonthlyFixedWithdraw(
                userId,
                lastMonth.getYear(),
                lastMonth.getMonthValue()
        );

        BigDecimal totalFixedCosts = totalMonthlyDebtPayment.add(monthlyFixedWithdraw);
        BigDecimal availableMonthlySpending = monthlyTransfer.subtract(totalFixedCosts);

        return availableMonthlySpending.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : availableMonthlySpending;
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

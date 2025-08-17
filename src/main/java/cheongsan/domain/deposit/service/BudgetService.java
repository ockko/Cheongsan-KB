package cheongsan.domain.deposit.service;

import cheongsan.domain.deposit.dto.BudgetLimitDTO;
import cheongsan.domain.deposit.dto.BudgetSettingStatusDTO;
import cheongsan.domain.deposit.dto.DailySpendingDTO;
import cheongsan.domain.user.entity.User;

public interface BudgetService {

    BudgetLimitDTO getBudgetLimits(Long userId);

    BudgetLimitDTO getBudgetLimitForDailySpending(User user);

    DailySpendingDTO saveFinalDailyLimit(Long userId, int finalDailyLimit);

    BudgetSettingStatusDTO getBudgetSettingStatus(Long userId);
}

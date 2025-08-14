package cheongsan.domain.deposit.service;

import cheongsan.domain.deposit.dto.BudgetLimitDTO;
import cheongsan.domain.deposit.dto.BudgetSettingStatusDTO;
import cheongsan.domain.deposit.dto.DailySpendingDTO;

public interface BudgetService {

    BudgetLimitDTO getBudgetLimits(Long userId);

    DailySpendingDTO saveFinalDailyLimit(Long userId, int finalDailyLimit);

    BudgetSettingStatusDTO getBudgetSettingStatus(Long userId);
}

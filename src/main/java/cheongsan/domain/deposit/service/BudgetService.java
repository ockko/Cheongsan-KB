package cheongsan.domain.deposit.service;

import cheongsan.domain.deposit.dto.BudgetLimitDTO;
import cheongsan.domain.deposit.dto.BudgetSettingStatusDTO;

public interface BudgetService {

    BudgetLimitDTO getBudgetLimits(Long userId);

    void saveFinalDailyLimit(Long userId, int finalDailyLimit);

    BudgetSettingStatusDTO getBudgetSettingStatus(Long userId);
}

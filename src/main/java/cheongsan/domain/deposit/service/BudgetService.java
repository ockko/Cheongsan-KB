package cheongsan.domain.deposit.service;

import cheongsan.domain.deposit.dto.BudgetLimitDTO;

public interface BudgetService {

    BudgetLimitDTO getBudgetLimits(Long userId);
}

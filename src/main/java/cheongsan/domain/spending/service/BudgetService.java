package cheongsan.domain.spending.service;

import cheongsan.domain.spending.dto.BudgetLimitDTO;

public interface BudgetService {

    BudgetLimitDTO getBudgetLimits(Long userId);
}

package cheongsan.domain.spending.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class BudgetLimitDTO {

    private final int recommendedDailyLimit;
    private final int maximumDailyLimit;
}

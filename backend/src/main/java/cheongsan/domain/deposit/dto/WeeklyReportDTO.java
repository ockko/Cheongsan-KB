package cheongsan.domain.deposit.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
@Builder
public class WeeklyReportDTO {

    private final String startDate;
    private final String endDate;
    private final double achievementRate;
    private final int dailyLimit;
    private final int averageDailySpending;
    private final Map<String, Integer> spendingByDay;
}

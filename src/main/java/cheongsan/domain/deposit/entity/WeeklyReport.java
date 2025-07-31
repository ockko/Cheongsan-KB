package cheongsan.domain.deposit.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyReport {

    private Long id;
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal achievementRate;
    private Integer dailyLimit;
    private Integer averageDailySpending;
    private String spendingByDay;
}

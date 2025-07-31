package cheongsan.domain.deposit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
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

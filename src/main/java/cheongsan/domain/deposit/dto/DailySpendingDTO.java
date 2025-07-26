package cheongsan.domain.deposit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DailySpendingDTO {

    private final int dailyLimit;
    private final int spent;
    private final int remaining;

    public static DailySpendingDTO getDailySpending(int dailyLimit, int spent) {
        int remaining = dailyLimit - spent;
        return new DailySpendingDTO(dailyLimit, spent, remaining);
    }
}

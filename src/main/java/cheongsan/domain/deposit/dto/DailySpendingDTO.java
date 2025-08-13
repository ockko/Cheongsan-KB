package cheongsan.domain.deposit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class DailySpendingDTO {

    private final int dailyLimit;
    private final int spent;
    private final boolean systemRecommended;
    private final int remaining;

    public static DailySpendingDTO getDailySpending(int dailyLimit, int spent, boolean isSystemRecommended) {
        int remaining = dailyLimit - spent;
        return new DailySpendingDTO(dailyLimit, spent, isSystemRecommended, remaining);
    }
}

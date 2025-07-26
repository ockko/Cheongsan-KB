package cheongsan.domain.deposit.dto;

import lombok.Data;

@Data
public class DailySpendingDTO {

    private final int dailyLimit;
    private final int spent;
    private final int remaining;

    public DailySpendingDTO(int dailyLimit, int spent) {
        this.dailyLimit = dailyLimit;
        this.spent = spent;
        this.remaining = dailyLimit - spent;
    }
}

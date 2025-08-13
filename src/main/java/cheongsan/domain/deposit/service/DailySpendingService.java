package cheongsan.domain.deposit.service;

import cheongsan.domain.deposit.dto.DailySpendingDTO;

public interface DailySpendingService {
    DailySpendingDTO getDailySpendingStatus(Long userId);
}

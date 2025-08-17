package cheongsan.domain.deposit.service;

import cheongsan.common.constant.ResponseMessage;
import cheongsan.domain.deposit.dto.DailySpendingDTO;
import cheongsan.domain.deposit.mapper.DepositMapper;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Log4j2
@Service
@RequiredArgsConstructor
public class DailySpendingServiceImpl implements DailySpendingService {
    private final UserMapper userMapper;
    private final DepositMapper depositMapper;
    private final BudgetService budgetService;
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);
    LocalDateTime startOfToday = today.atStartOfDay();      // 2025-08-18T00:00:00
    LocalDateTime startOfTomorrow = tomorrow.atStartOfDay(); // 2025-08-19T00:00:00

    @Override
    public DailySpendingDTO getDailySpendingStatus(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException(ResponseMessage.USER_NOT_FOUND.getMessage());
        }

        int dailyLimit;
        boolean isRecommended;

        if (user.getDailyLimit() != null && user.getDailyLimit().compareTo(BigDecimal.ZERO) > 0) {
            dailyLimit = user.getDailyLimit().intValue();
            isRecommended = false;
        } else {
            dailyLimit = budgetService.getBudgetLimitForDailySpending(user).getRecommendedDailyLimit();
            isRecommended = true;
        }

        BigDecimal todaySpent = depositMapper.sumTodaySpendingByUserIdAndDate(userId,startOfToday,startOfTomorrow);

        return DailySpendingDTO.getDailySpending(dailyLimit, todaySpent.intValue(), isRecommended);
    }
}

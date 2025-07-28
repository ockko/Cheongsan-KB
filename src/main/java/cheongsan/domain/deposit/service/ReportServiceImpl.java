package cheongsan.domain.deposit.service;

import cheongsan.common.constant.ResponseMessage;
import cheongsan.domain.deposit.dto.WeeklyReportDTO;
import cheongsan.domain.deposit.entity.Transaction;
import cheongsan.domain.deposit.mapper.DepositMapper;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final UserMapper userMapper;
    private final DepositMapper depositMapper;

    @Override
    public WeeklyReportDTO getLatestWeeklyReport(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException(ResponseMessage.USER_NOT_FOUND.getMessage());
        }
        int dailyLimit = (user.getDailyLimit() != null) ? user.getDailyLimit().intValue() : 0;

        LocalDate today = LocalDate.now();
        LocalDate lastMonday = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastSunday = today.minusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<Transaction> lastWeekWithdraws = depositMapper.findWithdrawTransactionsByPeriod(userId, lastMonday, lastSunday);

        Map<DayOfWeek, Integer> spendingByDayMap = calculateSpendingByDay(lastWeekWithdraws);
        long totalSpending = spendingByDayMap.values().stream().mapToLong(Integer::longValue).sum();
        long daysWithinLimit = spendingByDayMap.values().stream().filter(spent -> spent <= dailyLimit).count();

        double achievementRate = (daysWithinLimit / 7.0) * 100.0;
        int averageDailySpending = (int) (totalSpending / 7);

        return WeeklyReportDTO.builder()
                .startDate(lastMonday.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .endDate(lastSunday.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .achievementRate(achievementRate)
                .dailyLimit(dailyLimit)
                .averageDailySpending(averageDailySpending)
                .spendingByDay(formatSpendingByDay(spendingByDayMap))
                .build();
    }

    private Map<DayOfWeek, Integer> calculateSpendingByDay(List<Transaction> withdraws) {
        Map<DayOfWeek, Integer> spendingByDay = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            spendingByDay.put(day, 0); // 모든 요일을 0으로 초기화
        }

        for (Transaction withdraw : withdraws) {
            DayOfWeek day = withdraw.getTransactionTime().getDayOfWeek();
            spendingByDay.compute(day, (k, currentAmount) -> currentAmount + withdraw.getAmount().intValue());
        }
        return spendingByDay;
    }

    private Map<String, Integer> formatSpendingByDay(Map<DayOfWeek, Integer> spendingByDayMap) {
        Map<String, Integer> formattedMap = new LinkedHashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            formattedMap.put(day.name().substring(0, 3), spendingByDayMap.get(day));
        }
        return formattedMap;
    }
}

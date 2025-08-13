package cheongsan.domain.deposit.service;

import cheongsan.common.constant.ResponseMessage;
import cheongsan.domain.deposit.dto.WeeklyReportDTO;
import cheongsan.domain.deposit.entity.Transaction;
import cheongsan.domain.deposit.entity.WeeklyReport;
import cheongsan.domain.deposit.mapper.DepositMapper;
import cheongsan.domain.deposit.mapper.ReportMapper;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReportCreationServiceImpl implements ReportCreationService {
    private final UserMapper userMapper;
    private final DepositMapper depositMapper;
    private final ReportMapper reportMapper;
    private final ObjectMapper objectMapper;
    private final BudgetService budgetService;

    @Override
    @Transactional
    public WeeklyReportDTO createAndSaveLatestWeeklyReport(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException(ResponseMessage.USER_NOT_FOUND.getMessage());
        }

        int dailyLimit;
        if (user.getDailyLimit() != null && user.getDailyLimit().compareTo(BigDecimal.ZERO) > 0) {
            dailyLimit = user.getDailyLimit().intValue();
        } else {
            try {
                dailyLimit = budgetService.getBudgetLimits(userId).getRecommendedDailyLimit();
            } catch (IllegalStateException e) {
                // 소득 정보가 없어 추천 한도 계산조차 불가능한 경우, 리포트 생성을 중단
                log.warn("사용자(ID:{})의 소득 정보가 없어 리포트 생성을 건너뜁니다.", userId);
                throw new IllegalStateException("리포트를 생성하려면 소득 및 한도 정보가 필요합니다.");
            }
        }

        LocalDate today = LocalDate.now();
        LocalDate lastMonday = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastSunday = today.minusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<Transaction> lastWeekWithdraws = depositMapper.findWithdrawTransactionsByPeriod(userId, lastMonday, lastSunday);

        Map<DayOfWeek, Integer> spendingByDayMap = calculateSpendingByDay(lastWeekWithdraws);
        long totalSpending = spendingByDayMap.values().stream().mapToLong(Integer::longValue).sum();
        long daysWithinLimit = spendingByDayMap.values().stream().filter(spent -> spent <= dailyLimit).count();

        double achievementRate = (daysWithinLimit / 7.0) * 100.0;
        int averageDailySpending = (int) (totalSpending / 7);

        try {
            String spendingByDayJson = objectMapper.writeValueAsString(formatSpendingByDay(spendingByDayMap));
            WeeklyReport reportToSave = WeeklyReport.builder()
                    .userId(userId)
                    .startDate(lastMonday)
                    .endDate(lastSunday)
                    .achievementRate(BigDecimal.valueOf(achievementRate))
                    .dailyLimit(dailyLimit)
                    .averageDailySpending(averageDailySpending)
                    .spendingByDay(spendingByDayJson)
                    .build();
            reportMapper.save(reportToSave);
        } catch (Exception e) {
            throw new RuntimeException(ResponseMessage.FAILED_TO_SAVE_REPORT.getMessage());
        }

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
            spendingByDay.compute(day, (k, currentAmount) ->
                    (currentAmount == null ? 0 : currentAmount) + withdraw.getAmount().intValue()
            );
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

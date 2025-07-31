package cheongsan.domain.deposit.service;

import cheongsan.common.constant.ResponseMessage;
import cheongsan.common.util.DateUtils;
import cheongsan.domain.deposit.dto.WeeklyReportDTO;
import cheongsan.domain.deposit.dto.WeeklyReportHistoryDTO;
import cheongsan.domain.deposit.entity.Transaction;
import cheongsan.domain.deposit.entity.WeeklyReport;
import cheongsan.domain.deposit.mapper.DepositMapper;
import cheongsan.domain.deposit.mapper.ReportMapper;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final UserMapper userMapper;
    private final DepositMapper depositMapper;
    private final ReportMapper reportMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public WeeklyReportDTO createAndSaveLatestWeeklyReport(Long userId) {
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

    @Override
    public WeeklyReportDTO getLatestReportFromHistory(Long userId) {
        WeeklyReport latestReportEntity = reportMapper.findLatestByUserId(userId);
        if (latestReportEntity == null)
            throw new IllegalStateException(ResponseMessage.WEEKLY_REPORT_NOT_FOUND.getMessage());
        return convertToDTO(latestReportEntity);
    }

    @Override
    public WeeklyReportDTO getReportByDate(Long userId, LocalDate date) {
        WeeklyReport reportEntity = reportMapper.findByDate(userId, date);
        if (reportEntity == null)
            throw new IllegalStateException(ResponseMessage.WEEKLY_REPORT_NOT_FOUND_FOR_DATE.getMessage());
        return convertToDTO(reportEntity);
    }

    @Override
    public List<WeeklyReportHistoryDTO> getReportHistoryList(Long userId) {
        List<WeeklyReport> reportEntities = reportMapper.findAllByUserId(userId);
        return reportEntities.stream()
                .map(this::toHistoryDTO)
                .collect(Collectors.toList());
    }

    private WeeklyReportHistoryDTO toHistoryDTO(WeeklyReport entity) {
        LocalDate startDate = entity.getStartDate();
        int weekOfMonth = DateUtils.getWeekOfMonth(startDate);
        return new WeeklyReportHistoryDTO(
                entity.getId(),
                startDate.getYear(),
                startDate.getMonthValue(),
                weekOfMonth,
                startDate.toString(),
                entity.getEndDate().toString()
        );
    }

    private WeeklyReportDTO convertToDTO(WeeklyReport entity) {
        try {
            Map<String, Integer> spendingMap = objectMapper.readValue(entity.getSpendingByDay(), new TypeReference<Map<String, Integer>>() {
            });
            return WeeklyReportDTO.builder()
                    .startDate(entity.getStartDate().toString())
                    .endDate(entity.getEndDate().toString())
                    .achievementRate(entity.getAchievementRate().doubleValue())
                    .dailyLimit(entity.getDailyLimit())
                    .averageDailySpending(entity.getAverageDailySpending())
                    .spendingByDay(spendingMap)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(ResponseMessage.FAILED_TO_CONVERT_REPORT_DATA.getMessage());
        }
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

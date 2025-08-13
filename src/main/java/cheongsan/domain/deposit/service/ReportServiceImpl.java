package cheongsan.domain.deposit.service;

import cheongsan.common.constant.ResponseMessage;
import cheongsan.common.util.DateUtils;
import cheongsan.domain.deposit.dto.WeeklyReportDTO;
import cheongsan.domain.deposit.dto.WeeklyReportHistoryDTO;
import cheongsan.domain.deposit.entity.WeeklyReport;
import cheongsan.domain.deposit.mapper.ReportMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportMapper reportMapper;
    private final ObjectMapper objectMapper;
    private final ReportCreationService reportCreationService;

    @Override
    public WeeklyReportDTO getDashboardWeeklyReport(Long userId) {
        WeeklyReport latestReportEntity = reportMapper.findLatestByUserId(userId);

        if (latestReportEntity != null) {
            return convertToDTO(latestReportEntity);
        } else {
            try {
                return reportCreationService.createAndSaveLatestWeeklyReport(userId);
            } catch (IllegalStateException e) {
                log.warn("사용자(ID:{})의 신규 리포트를 생성할 수 없습니다: {}", userId, e.getMessage());
                return null;
            }
        }
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
}

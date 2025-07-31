package cheongsan.domain.deposit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class WeeklyReportHistoryDTO {

    private final Long reportId;
    private final int year;
    private final int month;
    private final int weekOfMonth;
    private final String startDate;
    private final String endDate;
}

package cheongsan.domain.debt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * 월별 상환일자 조회용 DTO
 */
@Data
public class RepaymentCalendarDTO {
    private Long debtId;
    private String debtName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate repaymentDate;
}

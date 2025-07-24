package cheongsan.domain.debt.dto;

import lombok.Data;

/**
 * 일별 상환일자 상세 조회용 DTO
 */
@Data
public class DailyRepaymentDTO {
    private Long debtId;
    private String debtName;
    private String organizationName;
}

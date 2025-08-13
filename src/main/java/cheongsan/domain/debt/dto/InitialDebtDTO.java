package cheongsan.domain.debt.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InitialDebtDTO {
    private Long debtId;
    private String organizationName; // 대출 기관명
    private String debtName; // 대출명
}

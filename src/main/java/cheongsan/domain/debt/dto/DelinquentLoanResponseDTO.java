package cheongsan.domain.debt.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DelinquentLoanResponseDTO {
    private String debtName;
    private String organizationName;
    private int overdueDays;
}

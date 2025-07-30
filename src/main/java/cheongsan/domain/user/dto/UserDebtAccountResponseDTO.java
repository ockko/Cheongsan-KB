package cheongsan.domain.user.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserDebtAccountResponseDTO {
    private Long debtId;
    private Long organization_code;
    private String organization_name;
    private String debt_name;
}

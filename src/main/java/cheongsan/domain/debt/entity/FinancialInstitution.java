package cheongsan.domain.debt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialInstitution {
    private String organizationCode;
    private String organizationName;
}

package cheongsan.domain.debt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialInstitution {
    private Long organizationCode;
    private String organizationName;
    private String InstitutionType;
    private Date createAt;
}

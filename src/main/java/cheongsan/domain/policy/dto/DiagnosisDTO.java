package cheongsan.domain.policy.dto;

import cheongsan.domain.policy.entity.Diagnosis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisDTO {
    private Long id;
    private String programName;
    private String operatingEntity;
    private String eligibleDebtors;
    private String eligibleDebts;
    private String debtAmountLimit;
    private String advantages;
    private String cautions;
    private String simpleDescription;

    // Diagnosis Entity → DTO 변환 static 메서드 (Builder 활용)
    public static DiagnosisDTO of(Diagnosis entity) {
        return DiagnosisDTO.builder()
                .id(entity.getId())
                .programName(entity.getProgramName())
                .operatingEntity(entity.getOperatingEntity())
                .eligibleDebtors(entity.getEligibleDebtors())
                .eligibleDebts(entity.getEligibleDebts())
                .debtAmountLimit(entity.getDebtAmountLimit())
                .advantages(entity.getAdvantages())
                .cautions(entity.getCautions())
                .simpleDescription(entity.getSimpleDescription())
                .build();
    }

}

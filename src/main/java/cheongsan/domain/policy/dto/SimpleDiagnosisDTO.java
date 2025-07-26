package cheongsan.domain.policy.dto;

import cheongsan.domain.policy.entity.SimpleDiagnosis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleDiagnosisDTO {

    private Long id;
    private String programName;
    private String operatingEntity;
    private String simpleDescription;

    // Diagnosis Entity → DTO 변환 static 메서드 (Builder 활용)
    static SimpleDiagnosisDTO of(SimpleDiagnosis simpleDiagnosis) {
        return SimpleDiagnosisDTO.builder()
                .id(simpleDiagnosis.getId())
                .programName(simpleDiagnosis.getProgramName())
                .operatingEntity(simpleDiagnosis.getOperatingEntity())
                .simpleDescription(simpleDiagnosis.getSimpleDescription())
                .build();
    }
}

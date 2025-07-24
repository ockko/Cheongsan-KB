package cheongsan.domain.policy.dto;

import cheongsan.domain.policy.entity.DiagnosisEntity;
import lombok.Getter;

@Getter
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

    //
    public DiagnosisDTO(DiagnosisEntity entity) {
        this.id = entity.getId();
        this.programName = entity.getProgramName();
        this.operatingEntity = entity.getOperatingEntity();
        this.eligibleDebtors = entity.getEligibleDebtors();
        this.eligibleDebts = entity.getEligibleDebts();
        this.debtAmountLimit = entity.getDebtAmountLimit();
        this.advantages = entity.getAdvantages();
        this.cautions = entity.getCautions();
        this.simpleDescription = entity.getSimpleDescription();
    }

}

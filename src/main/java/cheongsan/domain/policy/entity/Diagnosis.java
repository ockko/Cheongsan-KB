package cheongsan.domain.policy.entity;

import lombok.Getter;

@Getter
public class Diagnosis {
    private Long id;
    private String programName;
    private String operatingEntity;
    private String eligibleDebtors;
    private String eligibleDebts;
    private String debtAmountLimit;
    private String advantages;
    private String cautions;
    private String simpleDescription;
}

package cheongsan.domain.simulator.dto;

public enum RepaymentType {
    EQUAL_PAYMENT("원리금균등상환"), // 원리금 균등 상환
    EQUAL_PRINCIPAL("원금균등상환"), // 원금 균등 상환
    LUMP_SUM("만기일시상환"); // 만기 일시 상환

    private final String repaymentMethod;

    RepaymentType(String repaymentMethod) {
        this.repaymentMethod = repaymentMethod;
    }

    public static RepaymentType fromRepaymentMethod(String method) {
        if (method == null) return null;
        for (RepaymentType type : values()) {
            if (type.repaymentMethod.equals(method)) {
                return type;
            }
        }
        return null; // 또는 기본값 반환
    }
}

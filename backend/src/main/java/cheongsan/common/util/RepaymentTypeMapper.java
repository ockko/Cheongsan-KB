package cheongsan.common.util;

import cheongsan.domain.simulation.dto.RepaymentType;

public class RepaymentTypeMapper {
    public static LoanCalculator.RepaymentMethod toMethod(RepaymentType type) {
        if (type == null) {
            throw new IllegalArgumentException("상환 방식이 null입니다.");
        }

        switch (type) {
            case EQUAL_PAYMENT:
                return LoanCalculator.RepaymentMethod.EQUAL_PRINCIPAL_INTEREST;
            case EQUAL_PRINCIPAL:
                return LoanCalculator.RepaymentMethod.EQUAL_PRINCIPAL;
            case LUMP_SUM:
                return LoanCalculator.RepaymentMethod.BULLET_REPAYMENT;
            default:
                throw new IllegalArgumentException("지원하지 않는 상환 방식: " + type);
        }
    }

}

package cheongsan.domain.simulator.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanRecommendationRequestDTO {
    private BigDecimal principal;         // 대출 원금
    private BigDecimal interestRate;      // 이자율
    private int term;                     // 대출 기간 (개월 단위)
    private String repaymentType;         // 상환 방식
    private BigDecimal monthlyRepayment;  // 월 상환액
    private BigDecimal annualIncome;      // 연소득
}

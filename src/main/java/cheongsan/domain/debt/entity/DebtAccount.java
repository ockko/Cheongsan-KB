package cheongsan.domain.debt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

//debt_accounts 테이블 매핑
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtAccount {
    private Long id;
    private Long userId;
    private String organizationCode;
    private String resAccount;             // 계좌번호
    private String debtName;              // 대출명
    private BigDecimal currentBalance;    // 현재 잔액
    private BigDecimal originalAmount;    // 원금
    private BigDecimal interestRate;      // 이자율
    private LocalDate loanStartDate;      // 대출 시작일
    private LocalDate loanEndDate;        // 만기일
    private LocalDate nextPaymentDate;    // 다음 상환일
    private Long gracePeriodMonths;       // 거치기간 (월 단위)
    private String repaymentMethod;       // 상환 방식
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
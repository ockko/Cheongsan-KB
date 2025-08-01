package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDTO {

    private Long id;
    private String loanName; // 대출명
    private BigDecimal principal; // 남은 원금
    private BigDecimal interestRate; // 연이율(소수점)
    private LocalDate startDate; // 대출 시작일
    private LocalDate endDate; // 대출 만기일
    private String institutionType; // 금융1, 금융2, 기타
    private RepaymentType repaymentType; // 예: "원리금균등", "원금균등", "만기일시"
    private BigDecimal prepaymentFeeRate; // 중도상환수수료
    private LocalDate paymentDate; // 납부일

    public BigDecimal getMonthlyInterestRate() {
        return interestRate.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
    }

    public long getRemainingMonths(LocalDate now) {
        LocalDate effectiveStart = startDate.isAfter(now) ? startDate : now;
        return ChronoUnit.MONTHS.between(effectiveStart.withDayOfMonth(1), endDate.withDayOfMonth(1));
    }

}

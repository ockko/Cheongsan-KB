package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class LoanDTO {

    private Long id;
    private String loanName; // 대출명
    private BigDecimal principal; // 남은 원금
    private double interestRate; // 연이율(%)
    private BigDecimal monthlyPayment; // 월 납입금
    private LocalDate startDate; // 대출 시작일
    private LocalDate endDate; // 대출 만기일
    private String institutionType; // 금융1, 금융2, 기타
}

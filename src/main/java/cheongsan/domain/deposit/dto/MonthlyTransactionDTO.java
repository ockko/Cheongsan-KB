package cheongsan.domain.deposit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyTransactionDTO {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate transactionDate;

    private BigDecimal totalIncome;

    private BigDecimal totalExpense;

    private int transactionCount;

    // 계산된 필드를 위한 메서드
    public BigDecimal getNetAmount() {
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;
        return totalIncome.subtract(totalExpense);
    }
}

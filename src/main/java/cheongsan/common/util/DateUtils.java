package cheongsan.common.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    public static BigDecimal getRemainingMonths(LocalDate loanStartDate, LocalDate loanEndDate, LocalDate lastRepaymentDate) {
        LocalDate calculationStart = loanStartDate;
        if (lastRepaymentDate != null && lastRepaymentDate.isAfter(loanStartDate)) {
            calculationStart = lastRepaymentDate.plusMonths(1);
        }

        long remainingMonths = ChronoUnit.MONTHS.between(calculationStart, loanEndDate);

        if (remainingMonths < 0) {
            remainingMonths = 0;
        }

        return BigDecimal.valueOf(remainingMonths);
    }
}

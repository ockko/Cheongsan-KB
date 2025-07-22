package cheongsan.common.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    public static BigDecimal getMonthsBetween(LocalDate startDate, LocalDate endDate) {
        long months = ChronoUnit.MONTHS.between(startDate, endDate);
        return BigDecimal.valueOf(months);
    }

}

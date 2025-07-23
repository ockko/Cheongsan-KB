package cheongsan.domain.spending.service;

import java.math.BigDecimal;

public interface TransactionService {

    BigDecimal calculateRegularMonthlyTransfer(Long userId, int year, int month);
}

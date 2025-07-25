package cheongsan.domain.deposit.service;

import cheongsan.domain.deposit.dto.DailyTransactionDTO;
import cheongsan.domain.deposit.dto.MonthlyTransactionDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface DepositService {
    List<MonthlyTransactionDTO> getMonthlyTransactions(int year, int month);

    List<DailyTransactionDTO> getDailyTransactions(LocalDate date);

    BigDecimal calculateRegularMonthlyTransfer(Long userId, int year, int month);

    BigDecimal calculateMonthlyFixedWithdraw(Long userId, int year, int month);
}

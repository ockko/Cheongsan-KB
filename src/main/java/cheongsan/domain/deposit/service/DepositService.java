package cheongsan.domain.deposit.service;

import cheongsan.domain.deposit.dto.DailySpendingDTO;
import cheongsan.domain.deposit.dto.DailyTransactionDTO;
import cheongsan.domain.deposit.dto.MonthlyTransactionDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface DepositService {
    List<MonthlyTransactionDTO> getMonthlyTransactions(Long userId, int year, int month);

    List<DailyTransactionDTO> getDailyTransactions(Long userId, LocalDate date);

    BigDecimal calculateRegularMonthlyTransfer(Long userId, int year, int month);

    BigDecimal calculateMonthlyFixedWithdraw(Long userId, int year, int month);

    DailySpendingDTO getDailySpendingStatus(Long userId);
}

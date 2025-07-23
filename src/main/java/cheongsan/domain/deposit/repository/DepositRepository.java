package cheongsan.domain.deposit.repository;

import cheongsan.domain.deposit.dto.MonthlyTransactionDTO;
import cheongsan.domain.deposit.entity.Transaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DepositRepository {
    List<MonthlyTransactionDTO> getMonthlyTransactions(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );

    List<Transaction> getDailyTransactions(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );
}

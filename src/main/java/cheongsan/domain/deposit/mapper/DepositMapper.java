package cheongsan.domain.deposit.mapper;

import cheongsan.domain.deposit.dto.MonthlyTransactionDTO;
import cheongsan.domain.deposit.entity.Transaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DepositMapper {
    List<MonthlyTransactionDTO> getMonthlyTransactions(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );

    List<Transaction> getDailyTransactions(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );

    List<Transaction> findTransferTransactionsByMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );

    List<Transaction> findWithdrawTransactionsByMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );

    BigDecimal sumTodaySpendingByUserId(Long userId);

    List<Transaction> findWithdrawTransactionsByPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 거래내역 저장
    void insertTransaction(Transaction transaction);

    // 거래내역 중복 체크
    boolean isTransactionExists(@Param("userId") Long userId,
                                @Param("transactionTime") LocalDateTime transactionTime,
                                @Param("amount") BigDecimal amount,
                                @Param("type") String type);

    // 계좌별 최근 거래내역 조회
    List<Transaction> findRecentTransactionsByAccount(
            @Param("userId") Long userId,
            @Param("depositAccountId") Long depositAccountId,
            @Param("limit") int limit);
}

package cheongsan.domain.debt.mapper;

import cheongsan.domain.debt.entity.DebtTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DebtTransactionMapper {
    //대출 거래내역 저장
    void insertDebtTransaction(DebtTransaction debtTransaction);

    // 대출계좌별 거래내역 조회
    List<DebtTransaction> findByDebtAccountId(@Param("debtAccountId") Long debtAccountId);

    // 대출 거래내역 중복 체크
    boolean isDebtTransactionExists(@Param("debtAccountId") Long debtAccountId,
                                    @Param("transactionDate") LocalDate transactionDate);

    // 기간별 대출 거래내역 조회
    List<DebtTransaction> findByDebtAccountIdAndDateRange(
            @Param("debtAccountId") Long debtAccountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    List<DebtTransaction> findTransactionsByLoanAndPeriod(@Param("loanId") Long loanId,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);
}

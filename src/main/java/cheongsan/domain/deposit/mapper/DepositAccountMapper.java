package cheongsan.domain.deposit.mapper;

import cheongsan.domain.deposit.entity.DepositAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DepositAccountMapper {
    void insertDepositAccount(DepositAccount depositAccount);

    DepositAccount findByUserIdAndAccountNumber(
            @Param("userId") Long userId,
            @Param("accountNumber") String accountNumber);

    List<DepositAccount> findByUserId(@Param("userId") Long userId);

    void updateBalance(
            @Param("id") Long id,
            @Param("currentBalance") java.math.BigDecimal currentBalance);

    boolean isDepositAccountExists(
            @Param("userId") Long userId,
            @Param("accountNumber") String accountNumber);
}

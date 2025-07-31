package cheongsan.domain.deposit.mapper;

import cheongsan.domain.deposit.entity.DepositAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface DepositAccountMapper {
    // 예금계좌 정보 저장
    void insertDepositAccount(DepositAccount depositAccount);

    // 사용자ID와 계좌번호로 예금계좌 조회
    DepositAccount findByUserIdAndAccountNumber(@Param("userId") Long userId, @Param("accountNumber") String accountNumber);

    List<DepositAccount> findByUserId(@Param("userId") Long userId);

    // 예금계좌 잔액 업데이트
    void updateBalance(@Param("accountId") Long accountId, @Param("balance") BigDecimal balance);

    // 예금계좌 존재 여부 확인
    boolean isDepositAccountExists(@Param("userId") Long userId, @Param("accountNumber") String accountNumber);
}

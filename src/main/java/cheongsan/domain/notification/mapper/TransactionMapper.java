package cheongsan.domain.notification.mapper;

import cheongsan.domain.deposit.entity.Transaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionMapper {
    @Insert(
            "INSERT INTO transactions " +
                    "(deposit_account_id, user_id, transaction_time, amount, after_balance, type) " +
                    "VALUES " +
                    "(#{depositAccountId}, #{userId}, #{transactionTime}, #{amount}, #{afterBalance}, #{type})"
    )
    void insertTransaction(Transaction transaction);
}
package cheongsan.domain.notification.mapper;

import cheongsan.domain.notification.entity.Transactions;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionMapper {
    void insertTransaction(Transactions transaction);
}
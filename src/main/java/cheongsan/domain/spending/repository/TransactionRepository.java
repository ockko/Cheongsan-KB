package cheongsan.domain.spending.repository;

import cheongsan.domain.spending.dto.TransactionDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TransactionRepository {
    List<TransactionDTO> findTransferTransactionsByMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );
}

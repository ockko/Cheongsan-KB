package cheongsan.domain.spending.repository;

import cheongsan.common.config.RootConfig;
import cheongsan.domain.spending.dto.TransactionDTO;
import cheongsan.domain.spending.mapper.TransactionMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@Log4j2
class TransactionMapperTest {

    @Autowired
    private TransactionMapper mapper;

    @Test
    @DisplayName("월 이체 내역 조회")
    void findTransferTransactionsByMonth() {
        // given
        Long userId = 1L;
        int year = 2025;
        int month = 6;

        // when
        List<TransactionDTO> transactions = mapper.findTransferTransactionsByMonth(userId, year, month);

        // then
        for (TransactionDTO transaction : transactions) {
            log.info("========" + transaction);
        }
    }

    @Test
    @DisplayName("월 인출 내역 조회")
    void findWithdrawTransactionsByMonth() {
        // given
        Long userId = 1L;
        int year = 2025;
        int month = 6;

        // when
        List<TransactionDTO> transactions = mapper.findWithdrawTransactionsByMonth(userId, year, month);

        // then
        for (TransactionDTO transaction : transactions) {
            log.info("========" + transaction);
        }
    }
}
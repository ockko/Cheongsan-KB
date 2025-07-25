package cheongsan.domain.deposit.mapper;

import cheongsan.common.config.RootConfig;
import cheongsan.domain.deposit.entity.Transaction;
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
class DepositMapperTest {

    @Autowired
    private DepositMapper mapper;

    @Test
    @DisplayName("월 이체 내역 조회")
    void findTransferTransactionsByMonth() {
        // given
        Long userId = 1L;
        int year = 2025;
        int month = 6;

        // when
        List<Transaction> transactions = mapper.findTransferTransactionsByMonth(userId, year, month);

        // then
        for (Transaction transaction : transactions) {
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
        List<Transaction> transactions = mapper.findWithdrawTransactionsByMonth(userId, year, month);

        // then
        for (Transaction transaction : transactions) {
            log.info("========" + transaction);
        }
    }
}
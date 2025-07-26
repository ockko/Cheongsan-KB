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

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("오늘 지출 내역이 있는 사용자의 총 지출액을 정확히 합산해야 한다")
    void sumTodaySpendingByUserId_UserWithExpenses() {
        // given
        Long userId = 1L;

        // when
        BigDecimal totalSpending = mapper.sumTodaySpendingByUserId(userId);

        // then
        assertEquals(0, new BigDecimal("16800").compareTo(totalSpending), "오늘 지출액 합계가 일치해야 합니다.");
        log.info("오늘 지출이 있는 사용자(1L)의 총 지출액: " + totalSpending);
    }

    @Test
    @DisplayName("오늘 지출 내역이 없는 사용자의 총 지출액은 0이어야 한다")
    void sumTodaySpendingByUserId_UserWithNoExpenses() {
        // given
        Long userId = 2L;

        // when
        BigDecimal totalSpending = mapper.sumTodaySpendingByUserId(userId);

        // then
        assertEquals(0, BigDecimal.ZERO.compareTo(totalSpending), "오늘 지출이 없으면 합계는 0이어야 합니다.");
        log.info("오늘 지출이 없는 사용자(2L)의 총 지출액: " + totalSpending);
    }

    @Test
    @DisplayName("거래 내역이 전혀 없는 사용자의 총 지출액은 0이어야 한다")
    void sumTodaySpendingByUserId_UserWithNoTransactions() {
        // given
        Long userId = 3L;

        // when
        BigDecimal totalSpending = mapper.sumTodaySpendingByUserId(userId);

        // then
        assertEquals(0, BigDecimal.ZERO.compareTo(totalSpending), "거래 내역이 없으면 합계는 0이어야 합니다.");
        log.info("거래 내역이 없는 사용자(3L)의 총 지출액: " + totalSpending);
    }
}
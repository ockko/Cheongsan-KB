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
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@WebAppConfiguration
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

    @Test
    @DisplayName("특정 기간 동안의 지출 내역만 정확히 조회해야 한다")
    void findWithdrawTransactionsByPeriod() {
        // given
        Long userId = 1L;
        LocalDate today = LocalDate.now();
        LocalDate lastMonday = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastSunday = today.minusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // when
        List<Transaction> result = mapper.findWithdrawTransactionsByPeriod(userId, lastMonday, lastSunday);

        // then
        assertNotNull(result, "결과 리스트는 null이 아니어야 합니다.");
        assertEquals(2, result.size(), "User 1의 지난주 지출 내역은 2건이어야 합니다.");
        assertTrue(result.stream().allMatch(tx -> tx.getType().equals("WITHDRAW")), "조회된 모든 거래는 'WITHDRAW' 타입이어야 합니다.");
        log.info("조회된 거래내역 건수: " + result.size());
        log.info("조회된 거래내역: " + result);
    }

    @Test
    @DisplayName("해당 기간에 지출 내역이 없는 경우, 빈 리스트를 반환해야 한다")
    void findWithdrawTransactionsByPeriod_NoResult() {
        // given
        Long userId = 2L;
        LocalDate today = LocalDate.now();
        LocalDate lastMonday = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastSunday = today.minusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // when
        List<Transaction> result = mapper.findWithdrawTransactionsByPeriod(userId, lastMonday, lastSunday);

        // then
        assertNotNull(result, "결과 리스트는 null이 아니어야 합니다.");
        assertTrue(result.isEmpty(), "User 2의 지난주 지출 내역은 없으므로 결과는 빈 리스트여야 합니다.");
        log.info("지출 내역 없는 사용자의 조회 결과: " + result);
    }
}
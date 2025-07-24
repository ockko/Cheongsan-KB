package cheongsan.domain.spending.service;

import cheongsan.common.config.RootConfig;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@Log4j2
class TransactionServiceImplTest {

    @Autowired
    private TransactionServiceImpl transactionService;

    @Test
    @DisplayName("정기 소득 키워드가 포함된 거래내역의 합계를 정확히 계산해야 한다")
    void calculateRegularMonthlyTransfer_Success() {
        // given
        Long userId = 1L;
        int year = 2025;
        int month = 6;

        // when
        BigDecimal result = transactionService.calculateRegularMonthlyTransfer(userId, year, month);

        // then
        assertEquals(new BigDecimal("3150000.00"), result, "정기 소득 합계가 일치해야 합니다.");
        log.info("계산된 정기 소득 합계: " + result);
    }

    @Test
    @DisplayName("정기 소득 거래내역이 없으면 0을 반환해야 한다")
    void calculateRegularMonthlyTransfer_NoSalary() {
        // given
        Long userId = 1L;
        int year = 2025;
        int month = 7;

        // when
        BigDecimal result = transactionService.calculateRegularMonthlyTransfer(userId, year, month);

        // then
        assertEquals(BigDecimal.ZERO, result, "정기 소득이 없으면 합계는 0이어야 합니다.");
        log.info("계산된 정기 소득 합계: " + result);
    }

    @Test
    @DisplayName("고정 지출 키워드가 포함된 거래내역의 합계를 정확히 계산해야 한다")
    void calculateMonthlyFixedWithdraw_Success() {
        // given
        Long userId = 1L;
        int year = 2025;
        int month = 6;

        // when
        BigDecimal result = transactionService.calculateMonthlyFixedWithdraw(userId, year, month);

        // then
        assertEquals(new BigDecimal("336000.00"), result, "고정 지출 합계가 일치해야 합니다.");
        log.info("계산된 고정 지출 합계: " + result);
    }

    @Test
    @DisplayName("고정 지출 거래내역이 없으면 0을 반환해야 한다")
    void calculateMonthlyFixedWithdraw_NoFixedWithdraw() {
        // given
        Long userId = 1L;
        int year = 2025;
        int month = 7;

        // when
        BigDecimal result = transactionService.calculateMonthlyFixedWithdraw(userId, year, month);

        // then
        assertEquals(BigDecimal.ZERO, result, "고정 지출이 없으면 합계는 0이어야 합니다.");
        log.info("계산된 고정 지출 합계: " + result);
    }
}
package cheongsan.domain.deposit.service;

import cheongsan.common.config.RootConfig;
import cheongsan.domain.deposit.dto.DailySpendingDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@WebAppConfiguration
@Log4j2
class DepositServiceImplTest {

    @Autowired
    private DepositServiceImpl depositService;

    @Test
    @DisplayName("정기 소득 키워드가 포함된 거래내역의 합계를 정확히 계산해야 한다")
    void calculateRegularMonthlyTransfer_Success() {
        // given
        Long userId = 1L;
        int year = 2025;
        int month = 6;

        // when
        BigDecimal result = depositService.calculateRegularMonthlyTransfer(userId, year, month);

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
        BigDecimal result = depositService.calculateRegularMonthlyTransfer(userId, year, month);

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
        BigDecimal result = depositService.calculateMonthlyFixedWithdraw(userId, year, month);

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
        BigDecimal result = depositService.calculateMonthlyFixedWithdraw(userId, year, month);

        // then
        assertEquals(BigDecimal.ZERO, result, "고정 지출이 없으면 합계는 0이어야 합니다.");
        log.info("계산된 고정 지출 합계: " + result);
    }

    @Test
    @DisplayName("오늘의 지출 현황을 정확히 계산해야 한다")
    void getDailySpendingStatus() {
        // given
        Long userId = 1L;

        // when
        DailySpendingDTO result = depositService.getDailySpendingStatus(userId);

        // then
        assertNotNull(result);
        assertEquals(50000, result.getDailyLimit(), "한도가 나와야 합니다.");
        assertEquals(16800, result.getSpent(), "오늘 지출액 합계가 나와야 합니다.");
        assertEquals(33200, result.getRemaining(), "남은 금액이 정확해야 합니다.");
        log.info("오늘의 지출 현황 결과: " + result);
    }

    @Test
    @DisplayName("오늘 지출이 없는 경우, 지출액은 0이어야 한다")
    void getDailySpendingStatus_WithNoSpendingToday() {
        // given
        Long userId = 1L;

        // when
        DailySpendingDTO result = depositService.getDailySpendingStatus(userId);

        // then
        assertNotNull(result);
        assertEquals(50000, result.getDailyLimit());
        assertEquals(0, result.getSpent(), "오늘 지출이 없으므로 0이 나와야 합니다.");
        assertEquals(50000, result.getRemaining());
        log.info("오늘 지출 없는 사용자 결과: " + result);
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 경우 예외가 발생해야 한다")
    void getDailySpendingStatus_UserNotFound_ShouldThrowException() {
        // given
        Long nonExistentUserId = 999L;

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            depositService.getDailySpendingStatus(nonExistentUserId);
        });

        assertEquals("일치하는 회원 정보가 없습니다.", exception.getMessage());
        log.info("사용자 없음 예외 처리 성공: " + exception.getMessage());
    }
}
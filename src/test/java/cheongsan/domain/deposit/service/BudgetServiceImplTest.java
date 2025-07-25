package cheongsan.domain.deposit.service;

import cheongsan.common.config.RootConfig;
import cheongsan.domain.deposit.dto.BudgetLimitDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@Log4j2
class BudgetServiceImplTest {

    @Autowired
    private BudgetServiceImpl budgetService;

    @Test
    @DisplayName("추천 한도와 최대 한도를 DTO에 담아 정확히 반환해야 한다")
    void getBudgetLimits() {
        // given
        Long userId = 1L;

        // when
        BudgetLimitDTO result = budgetService.getBudgetLimits(userId);

        // then
        assertNotNull(result, "결과 DTO는 null이 아니어야 합니다.");
        assertEquals(51838, result.getRecommendedDailyLimit());
        assertEquals(74055, result.getMaximumDailyLimit());
        log.info("사용자 1의 계산된 한도: " + result);
    }

    @Test
    @DisplayName("소득 정보가 없는 사용자의 경우 IllegalStateException을 던져야 한다")
    void getBudgetLimits_NoTransfer_ShouldThrowException() {
        // given
        Long userId = 2L;

        // when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            budgetService.getBudgetLimits(userId);
        });

        assertEquals("추천 한도를 계산하기 위해 월 소득 정보가 필요합니다.", exception.getMessage());
        log.info("소득 정보 없는 사용자 예외 처리 성공: " + exception.getMessage());
    }

    @Test
    @DisplayName("고정 지출이 소득보다 많은 사용자의 경우 추천 한도는 0이 되어야 한다")
    void getBudgetLimits_WithdrawExceedsTransfer_ShouldReturnZero() {
        // given
        Long userId = 1L;

        // when
        BudgetLimitDTO result = budgetService.getBudgetLimits(userId);

        // then
        assertNotNull(result, "결과 DTO는 null이 아니어야 합니다.");
        assertEquals(0, result.getRecommendedDailyLimit(), "추천 한도는 0이어야 합니다.");
        assertEquals(0, result.getMaximumDailyLimit(), "최대 한도는 0이어야 합니다.");
        log.info("고정 지출 초과 사용자의 추천 한도: " + result);
    }
}
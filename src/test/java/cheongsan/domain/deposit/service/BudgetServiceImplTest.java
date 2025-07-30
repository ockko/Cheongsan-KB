package cheongsan.domain.deposit.service;

import cheongsan.common.config.RootConfig;
import cheongsan.domain.deposit.dto.BudgetLimitDTO;
import cheongsan.domain.deposit.dto.BudgetSettingStatusDTO;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@Log4j2
class BudgetServiceImplTest {

    @Autowired
    private BudgetServiceImpl budgetService;

    @Autowired
    private UserMapper userMapper;

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

    @Test
    @DisplayName("유효한 한도 금액을 성공적으로 저장해야 한다")
    void saveFinalDailyLimit() {
        // given
        Long userId = 1L;
        int newLimit = 50000;

        // when
        assertDoesNotThrow(() -> {
            budgetService.saveFinalDailyLimit(userId, newLimit);
        });

        // then
        User updatedUser = userMapper.findById(userId);
        assertNotNull(updatedUser);
        assertEquals(0, new BigDecimal(newLimit).compareTo(updatedUser.getDailyLimit()));
        assertNotNull(updatedUser.getDailyLimitDate());
        log.info("한도 저장 성공 후 사용자 정보: " + updatedUser);
    }

    @Test
    @DisplayName("최대 한도를 초과하는 금액은 예외가 발생해야 한다")
    void saveFinalDailyLimit_ExceedsMax_ShouldThrowException() {
        // given
        Long userId = 1L;
        int invalidLimit = 999999;

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            budgetService.saveFinalDailyLimit(userId, invalidLimit);
        });
    }

    @Test
    @DisplayName("주 1회 수정 규칙을 위반하면 예외가 발생해야 한다")
    void saveFinalDailyLimit_UpdateRuleViolated_ShouldThrowException() {
        // given
        Long userId = 1L;
        int newLimit = 10000;

        // when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            budgetService.saveFinalDailyLimit(userId, newLimit);
        });
        assertEquals("소비 한도는 주 1회만 수정할 수 있습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("마지막 수정일이 있는 경우, 해당 날짜를 정확히 반환해야 한다")
    void getBudgetSettingStatus_WhenDateExists() {
        // given
        Long userId = 1L;
        LocalDateTime expectedTimestamp = LocalDateTime.of(2025, 7, 26, 10, 30, 25);

        // when
        BudgetSettingStatusDTO result = budgetService.getBudgetSettingStatus(userId);

        // then
        assertNotNull(result);
        assertEquals(expectedTimestamp, result.getDailyLimitDate());
        log.info("조회된 마지막 수정일: " + result.getDailyLimitDate());
    }

    @Test
    @DisplayName("마지막 수정일이 없는 경우, null을 반환해야 한다")
    void getBudgetSettingStatus_WhenDateIsNull() {
        // given
        Long userId = 2L;

        // when
        BudgetSettingStatusDTO result = budgetService.getBudgetSettingStatus(userId);

        // then
        assertNotNull(result);
        assertNull(result.getDailyLimitDate());
        log.info("수정일이 없는 경우, null이 정상적으로 반환됨");
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 경우 예외가 발생해야 한다")
    void getBudgetSettingStatus_UserNotFound_ShouldThrowException() {
        // given
        Long nonExistentUserId = 999L;

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            budgetService.getBudgetSettingStatus(nonExistentUserId);
        });

        assertEquals("일치하는 회원 정보가 없습니다.", exception.getMessage());
        log.info("사용자 없음 예외 처리 성공: " + exception.getMessage());
    }
}
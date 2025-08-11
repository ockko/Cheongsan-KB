package cheongsan.domain.deposit.scheduler;

import cheongsan.common.config.RootConfig;
import cheongsan.common.config.ThymeleafConfig;
import cheongsan.domain.notification.mapper.NotificationMapper;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class, ThymeleafConfig.class})
@WebAppConfiguration
@Log4j2
@Sql(scripts = "/sql/budget-scheduler-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class BudgetUpdateSchedulerTest {
    @Autowired
    private BudgetUpdateScheduler budgetUpdateScheduler;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Test
    @DisplayName("스케줄러 실행 시, 유효하지 않은 한도를 가진 사용자는 자동 조정하고 알림을 생성해야 한다")
    void updateUserBudgets() {
        // given
        // SQL 스크립트를 통해 모든 데이터가 준비된 상태

        // when
        // 스케줄러의 public 메소드를 직접 호출하여 실행
        budgetUpdateScheduler.updateUserBudgets();

        // then

        // --- 1. User 1 (자동 조정 대상) 검증 ---
        User adjustedUser = userMapper.findById(1L);
        // 예상 계산:
        // 월 소득(100만) - 부채상환(5만) - 고정지출(10만) = 85만
        // 최대 한도: (85만 * 0.5) / 30 = 14,166원
        // 추천 한도: 14,166 * 0.7 = 9,916원 -> 500단위 내림 -> 9,500원
        BigDecimal expectedLimit = new BigDecimal("9500");

        assertNotNull(adjustedUser);
        assertEquals(0, expectedLimit.compareTo(adjustedUser.getDailyLimit()), "User 1의 한도는 새로운 추천 한도로 조정되어야 합니다.");

        int alertCountUser1 = notificationMapper.countTodayNotificationsByType(1L, "BUDGET_ADJUSTED");
        assertEquals(1, alertCountUser1, "User 1에게는 한도 조정 알림이 생성되어야 합니다.");

        log.info("자동 조정된 사용자(1L)의 한도: " + adjustedUser.getDailyLimit());


        // --- 2. User 2 (한도 유지 대상) 검증 ---
        User maintainedUser = userMapper.findById(2L);
        assertNotNull(maintainedUser);
        assertEquals(0, new BigDecimal("10000").compareTo(maintainedUser.getDailyLimit()), "User 2의 한도는 기존 값을 유지해야 합니다.");

        int alertCountUser2 = notificationMapper.countTodayNotificationsByType(2L, "BUDGET_ADJUSTED");
        assertEquals(0, alertCountUser2, "User 2에게는 알림이 생성되지 않아야 합니다.");
        log.info("유지된 사용자(2L)의 한도: " + maintainedUser.getDailyLimit());
    }
}
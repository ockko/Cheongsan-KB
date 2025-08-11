package cheongsan.domain.deposit.service;

import cheongsan.common.config.RootConfig;
import cheongsan.domain.deposit.mapper.DepositMapper;
import cheongsan.domain.notification.mapper.NotificationMapper;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@WebAppConfiguration
@Log4j2
class LimitCheckServiceImplTest {

    @Autowired
    private LimitCheckServiceImpl limitCheckService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DepositMapper depositMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Test
    @DisplayName("한도를 초과한 사용자에게 메일을 발송하고 DB에 기록해야 한다")
    void checkDailyLimitExceeded_ShouldNotifyAndSave() {
        // given
        Long userId = 1L;
        log.info("실제 이메일 발송 테스트를 시작합니다...");

        // when
        assertDoesNotThrow(() -> {
            limitCheckService.checkDailyLimitExceeded(userId);
        });

        // then
        int notifyCount = notificationMapper.countTodayNotificationsByType(userId, "DAILY_LIMIT_EXCEEDED");
        assertEquals(1, notifyCount, "DB에 한도 초과 알림이 1건 기록되어야 합니다.");

        log.info("이메일 발송 요청이 성공적으로 완료되었습니다. 메일함을 확인해주세요.");
    }

    @Test
    @DisplayName("현재 DB 상황을 확인한다")
    void checkCurrentDatabaseStatus() {
        log.info("=== 현재 DB 상황 확인 ===");

        // 1. Connected ID가 있는 모든 사용자 조회
        List<User> activeUsers = userMapper.findUsersWithConnectedId();
        log.info("Connected ID가 있는 사용자 수: {}", activeUsers.size());

        for (User user : activeUsers) {
            log.info("사용자 ID: {}, user_id: {}, connected_id: {}, daily_limit: {}",
                    user.getId(), user.getUserId(), user.getConnectedId(), user.getDailyLimit());

            // 각 사용자의 오늘 지출 확인
            BigDecimal todaySpent = depositMapper.sumTodaySpendingByUserId(user.getId());
            log.info("사용자 ID {} 오늘 지출: {}원", user.getId(), todaySpent);

            // 각 사용자의 오늘 알림 개수 확인
            int notificationCount = notificationMapper.countTodayNotificationsByType(user.getId(), "DAILY_LIMIT_EXCEEDED");
            log.info("사용자 ID {} 오늘 한도 초과 알림 개수: {}개", user.getId(), notificationCount);

            log.info("---");
        }
    }

    @Test
    @Transactional
    @DisplayName("실제 DB 데이터로 전체 사용자 일일 한도 체크를 실행한다")
    void testCheckAllUsersDailyLimitExceeded_WithRealData() {
        log.info("=== 실제 DB 데이터로 전체 사용자 일일 한도 체크 테스트 ===");

        // 테스트 전 상태 확인
        List<User> activeUsers = userMapper.findUsersWithConnectedId();
        log.info("테스트 시작 - 활성 사용자 수: {}", activeUsers.size());

        // 테스트 전 각 사용자의 알림 개수 기록
        int totalNotificationsBefore = 0;
        for (User user : activeUsers) {
            int count = notificationMapper.countTodayNotificationsByType(user.getId(), "DAILY_LIMIT_EXCEEDED");
            totalNotificationsBefore += count;
            log.info("테스트 전 - 사용자 ID {}: 기존 알림 {}개", user.getId(), count);
        }

        // when: 전체 사용자 한도 체크 실행
        assertDoesNotThrow(() -> {
            limitCheckService.checkAllUsersDailyLimitExceeded();
        }, "전체 사용자 한도 체크가 정상적으로 실행되어야 합니다");

        // then: 결과 확인
        int totalNotificationsAfter = 0;
        int exceededUsers = 0;

        for (User user : activeUsers) {
            BigDecimal dailyLimit = user.getDailyLimit();
            BigDecimal todaySpent = depositMapper.sumTodaySpendingByUserId(user.getId());
            int notificationCount = notificationMapper.countTodayNotificationsByType(user.getId(), "DAILY_LIMIT_EXCEEDED");

            totalNotificationsAfter += notificationCount;

            log.info("테스트 후 - 사용자 ID {}: 한도={}원, 지출={}원, 알림={}개",
                    user.getId(), dailyLimit, todaySpent, notificationCount);

            // 한도 초과한 사용자인지 확인
            if (dailyLimit != null && dailyLimit.compareTo(BigDecimal.ZERO) > 0 &&
                    todaySpent.compareTo(dailyLimit) > 0) {
                exceededUsers++;
                assertTrue(notificationCount > 0,
                        "한도를 초과한 사용자 ID " + user.getId() + "는 알림을 받아야 합니다");
            }
        }

        log.info("=== 테스트 결과 요약 ===");
        log.info("활성 사용자 수: {}", activeUsers.size());
        log.info("한도 초과 사용자 수: {}", exceededUsers);
        log.info("테스트 전 총 알림 수: {}", totalNotificationsBefore);
        log.info("테스트 후 총 알림 수: {}", totalNotificationsAfter);
        log.info("새로 생성된 알림 수: {}", totalNotificationsAfter - totalNotificationsBefore);
    }

    @Test
    @DisplayName("특정 사용자의 한도를 조작해서 알림 발송을 테스트한다")
    @Transactional
    void testLimitExceededScenario() {
        log.info("=== 한도 초과 시나리오 테스트 ===");

        // given: 테스트할 사용자 선택 (Connected ID가 있는 첫 번째 사용자)
        List<User> activeUsers = userMapper.findUsersWithConnectedId();
        if (activeUsers.isEmpty()) {
            log.warn("테스트할 활성 사용자가 없습니다");
            return;
        }

        User testUser = activeUsers.get(0);
        BigDecimal currentSpending = depositMapper.sumTodaySpendingByUserId(testUser.getId());

        log.info("테스트 사용자: ID={}, 현재 지출={}원", testUser.getId(), currentSpending);

        // 현재 지출보다 낮은 한도 설정 (강제로 한도 초과 상황 만들기)
        BigDecimal testLimit = currentSpending.subtract(new BigDecimal("1000")); // 1000원 낮게 설정
        if (testLimit.compareTo(BigDecimal.ZERO) <= 0) {
            testLimit = new BigDecimal("1"); // 최소 1원으로 설정
        }

        userMapper.updateDailyLimitAndTimestamp(testUser.getId(), testLimit, LocalDateTime.now());
        log.info("한도를 {}원으로 설정 (현재 지출보다 낮음)", testLimit);

        // 기존 알림 개수 확인
        int notificationsBefore = notificationMapper.countTodayNotificationsByType(testUser.getId(), "DAILY_LIMIT_EXCEEDED");
        log.info("테스트 전 알림 개수: {}", notificationsBefore);

        // when: 한도 체크 실행
        limitCheckService.checkAllUsersDailyLimitExceeded();

        // then: 결과 확인
        int notificationsAfter = notificationMapper.countTodayNotificationsByType(testUser.getId(), "DAILY_LIMIT_EXCEEDED");
        log.info("테스트 후 알림 개수: {}", notificationsAfter);

        if (currentSpending.compareTo(BigDecimal.ZERO) > 0) {
            assertTrue(notificationsAfter > notificationsBefore,
                    "한도를 초과했으므로 새로운 알림이 생성되어야 합니다");
            log.info("✅ 한도 초과 알림이 정상적으로 생성되었습니다");
        } else {
            log.info("현재 지출이 0원이므로 알림이 생성되지 않았습니다");
        }
    }

    @Test
    @DisplayName("중복 알림 방지 기능을 테스트한다")
    @Transactional
    void testDuplicateNotificationPrevention() {
        log.info("=== 중복 알림 방지 테스트 ===");

        // given: 활성 사용자 중 한도가 설정된 사용자 찾기
        List<User> activeUsers = userMapper.findUsersWithConnectedId();
        User testUser = null;

        for (User user : activeUsers) {
            if (user.getDailyLimit() != null && user.getDailyLimit().compareTo(BigDecimal.ZERO) > 0) {
                testUser = user;
                break;
            }
        }

        if (testUser == null) {
            log.warn("한도가 설정된 테스트 사용자가 없습니다");
            return;
        }

        log.info("테스트 사용자: ID={}, 설정 한도={}원", testUser.getId(), testUser.getDailyLimit());

        // when: 같은 메서드를 연속으로 2번 실행
        limitCheckService.checkAllUsersDailyLimitExceeded();
        int notificationsAfterFirst = notificationMapper.countTodayNotificationsByType(testUser.getId(), "DAILY_LIMIT_EXCEEDED");

        limitCheckService.checkAllUsersDailyLimitExceeded(); // 두 번째 실행
        int notificationsAfterSecond = notificationMapper.countTodayNotificationsByType(testUser.getId(), "DAILY_LIMIT_EXCEEDED");

        // then: 알림 개수가 증가하지 않아야 함
        assertEquals(notificationsAfterFirst, notificationsAfterSecond,
                "중복 실행 시 알림이 추가로 생성되지 않아야 합니다");

        log.info("첫 번째 실행 후 알림 개수: {}", notificationsAfterFirst);
        log.info("두 번째 실행 후 알림 개수: {}", notificationsAfterSecond);
        log.info("✅ 중복 알림 방지가 정상적으로 작동합니다");
    }

    @Test
    @DisplayName("한도가 설정되지 않은 사용자는 체크하지 않는다")
    void testUsersWithoutLimit() {
        log.info("=== 한도 미설정 사용자 테스트 ===");

        List<User> activeUsers = userMapper.findUsersWithConnectedId();

        int usersWithoutLimit = 0;
        int usersWithLimit = 0;

        for (User user : activeUsers) {
            if (user.getDailyLimit() == null || user.getDailyLimit().compareTo(BigDecimal.ZERO) == 0) {
                usersWithoutLimit++;
                log.info("한도 미설정 사용자: ID={}, daily_limit={}", user.getId(), user.getDailyLimit());
            } else {
                usersWithLimit++;
                log.info("한도 설정 사용자: ID={}, daily_limit={}원", user.getId(), user.getDailyLimit());
            }
        }

        log.info("전체 활성 사용자: {}명", activeUsers.size());
        log.info("한도 설정 사용자: {}명", usersWithLimit);
        log.info("한도 미설정 사용자: {}명", usersWithoutLimit);

        // 실제 동작 확인
        assertDoesNotThrow(() -> {
            limitCheckService.checkAllUsersDailyLimitExceeded();
        }, "한도 미설정 사용자가 있어도 정상적으로 실행되어야 합니다");
    }
}
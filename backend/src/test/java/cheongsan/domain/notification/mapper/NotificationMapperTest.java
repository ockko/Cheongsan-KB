package cheongsan.domain.notification.mapper;

import cheongsan.common.config.RootConfig;
import cheongsan.domain.notification.entity.Notification;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@WebAppConfiguration
@Log4j2
class NotificationMapperTest {

    @Autowired
    private NotificationMapper notificationMapper;

    @Test
    @Transactional
    @DisplayName("새로운 알림을 성공적으로 저장하고, 생성된 ID를 반환해야 한다")
    void save() {
        // given
        Notification newNotification = Notification.builder()
                .userId(2L)
                .contents("테스트 메시지")
                .type("DAILY_LIMIT_EXCEEDED")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        notificationMapper.save(newNotification);

        // then
        assertNotNull(newNotification.getId(), "INSERT 후에는 ID가 생성되어야 합니다.");
        log.info("생성된 알림 ID: " + newNotification.getId());
    }

    @Test
    @DisplayName("오늘 특정 유형의 알림이 있는 경우, 정확한 개수를 반환해야 한다")
    void countTodayNotificationsByType_WhenNotificationsExist() {
        // given
        Long userId = 1L;
        String type = "DAILY_LIMIT_EXCEEDED";

        // when
        int count = notificationMapper.countTodayNotificationsByType(userId, type);

        // then
        assertEquals(1, count, "오늘 날짜로 생성된 한도 초과 알림은 1개여야 합니다.");
        log.info("조회된 알림 개수: " + count);
    }

    @Test
    @DisplayName("오늘 특정 유형의 알림이 없는 경우, 0을 반환해야 한다")
    void countTodayNotificationsByType_WhenNotificationsDoNotExist() {
        // given
        Long userId = 2L;
        String type = "DAILY_LIMIT_EXCEEDED";

        // when
        int count = notificationMapper.countTodayNotificationsByType(userId, type);

        // then
        assertEquals(0, count, "알림이 없으므로 0을 반환해야 합니다.");
        log.info("조회된 알림 개수: " + count);
    }
}
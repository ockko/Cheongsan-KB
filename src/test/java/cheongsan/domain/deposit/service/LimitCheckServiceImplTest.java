package cheongsan.domain.deposit.service;

import cheongsan.common.config.RootConfig;
import cheongsan.domain.notification.mapper.NotificationMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@WebAppConfiguration
@Log4j2
class LimitCheckServiceImplTest {

    @Autowired
    private LimitCheckServiceImpl limitCheckService;

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
}
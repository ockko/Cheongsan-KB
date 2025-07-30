package cheongsan.domain.notification.service;

import cheongsan.common.config.RootConfig;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { RootConfig.class })
@Log4j2
class NotificationServiceImplTest {

    @Autowired
    private NotificationServiceImpl notificationService;

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("실제 이메일 서버를 통해 한도 초과 알림 메일을 발송해야 한다")
    void sendDailyLimitExceededEmail() {
        // given
        Long userId = 1L;
        User user = userMapper.findById(userId);

        int dailyLimit = 50000;
        int totalSpent = 52000;

        log.info(user.getEmail() + " 주소로 실제 이메일 발송을 시도합니다...");

        // when & then
        assertDoesNotThrow(() -> {
            notificationService.sendDailyLimitExceededEmail(user, dailyLimit, totalSpent);
        });

        log.info("이메일 발송 요청이 성공적으로 완료되었습니다. 메일함을 확인해주세요.");
    }
}
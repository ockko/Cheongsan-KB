package cheongsan.domain.notification.service;

import cheongsan.common.config.RootConfig;
import cheongsan.domain.notification.dto.CreateNotificationDTO;
import cheongsan.domain.notification.entity.Notification;
import cheongsan.domain.notification.mapper.NotificationMapper;
import cheongsan.domain.notification.websocket.WebSocketManager;
import cheongsan.common.config.ThymeleafConfig;
import cheongsan.domain.deposit.dto.WeeklyReportDTO;
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

import java.util.Collections;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class, ThymeleafConfig.class})
@WebAppConfiguration
@Log4j2
class NotificationServiceImplTest {

    @Autowired
    private NotificationServiceImpl notificationService;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private WebSocketManager webSocketManager;

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

    @Test
    @DisplayName("실제 이메일 서버를 통해 주간 리포트 메일을 발송해야 한다")
    void sendWeeklyReportEmail() {
        // given
        Long userId = 1L;
        User user = userMapper.findById(userId);

        WeeklyReportDTO testReport = WeeklyReportDTO.builder()
                .startDate("2025-07-21")
                .endDate("2025-07-27")
                .achievementRate(57.14)
                .averageDailySpending(34000)
                .dailyLimit(28000)
                .spendingByDay(Collections.emptyMap())
                .build();

        log.info(user.getEmail() + " 주소로 실제 주간 리포트 이메일 발송을 시도합니다...");

        // when & then
        assertDoesNotThrow(() -> {
            notificationService.sendWeeklyReportEmail(user, testReport);
        });

        log.info("이메일 발송 요청이 성공적으로 완료되었습니다. 메일함을 확인해주세요.");
    }

    @Test
    public void testCreateNotification() throws Exception {
        CreateNotificationDTO dto = CreateNotificationDTO.builder()
                .userId(1L)
                .type("LIMIT_EXCEEDED")
                .contents("소비 한도를 초과했습니다!")
                .build();

        notificationService.createNotification(dto);

//        List<Notification> notis = notificationMapper.findByUserId(1L);
//        System.out.println(notis.size());
    }
}
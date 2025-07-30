package cheongsan.domain.deposit.service;

import cheongsan.domain.deposit.mapper.DepositMapper;
import cheongsan.domain.notification.entity.Notification;
import cheongsan.domain.notification.mapper.NotificationMapper;
import cheongsan.domain.notification.service.NotificationService;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Log4j2
@Service
@RequiredArgsConstructor
public class LimitCheckServiceImpl implements LimitCheckService {

    private final UserMapper userMapper;
    private final DepositMapper depositMapper;
    private final NotificationMapper notificationMapper;
    private final NotificationService notificationService;

    private static final String NOTIFICATION_TYPE_LIMIT_EXCEEDED = "DAILY_LIMIT_EXCEEDED";

    @Override
    @Transactional
    public void checkDailyLimitExceeded(Long userId) {
        int sentCount = notificationMapper.countTodayNotificationsByType(userId, NOTIFICATION_TYPE_LIMIT_EXCEEDED);
        if (sentCount > 0) {
            log.info("사용자(ID: {})는 오늘 이미 한도 초과 알림을 받았습니다. 중복 발송을 방지합니다.", userId);
            return; // 이미 보냈으면 로직 종료
        }

        User user = userMapper.findById(userId);
        if (user == null) {
            log.warn("사용자(ID: {}) 정보가 없어 검사를 건너뜁니다.", userId);
            return;
        }
        int dailyLimit = user.getDailyLimit().intValue();
        BigDecimal todaySpent = depositMapper.sumTodaySpendingByUserId(userId);

        if (todaySpent.intValue() > dailyLimit) {
            log.info("사용자(ID: {})의 한도 초과를 감지하여 알림 프로세스를 시작합니다.", userId);

            // 이메일 발송 (비동기 처리)
            notificationService.sendDailyLimitExceededEmail(user, dailyLimit, todaySpent.intValue());

            Notification newNotification = Notification.builder()
                    .userId(userId)
                    .contents(String.format("일일 한도(%d원)를 초과했습니다. (오늘 지출:%d원)", dailyLimit, todaySpent.intValue()))
                    .type(NOTIFICATION_TYPE_LIMIT_EXCEEDED)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            // 발송 사실을 DB에 저장
            notificationMapper.save(newNotification);
        }
    }
}

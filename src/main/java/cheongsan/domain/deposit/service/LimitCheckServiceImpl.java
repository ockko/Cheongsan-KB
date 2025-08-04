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
import java.util.List;

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

            // 웹소켓 알림 전송
            notificationService.sendAlarm(newNotification);
        }
    }

    @Override
    @Transactional
    public void checkAllUsersDailyLimitExceeded() {
        try {
            // Connected ID가 있는 모든 사용자 조회 (활성 사용자만)
            List<User> activeUsers = userMapper.findUsersWithConnectedId();

            log.info("전체 사용자 일일 한도 체크 시작: {}명", activeUsers.size());

            int checkedCount = 0;
            int exceededCount = 0;
            int alreadyNotifiedCount = 0;

            for (User user : activeUsers) {
                try {
                    // 이미 오늘 알림을 받았는지 확인
                    int sentCount = notificationMapper.countTodayNotificationsByType(user.getId(), NOTIFICATION_TYPE_LIMIT_EXCEEDED);
                    if (sentCount > 0) {
                        log.debug("사용자(ID: {})는 오늘 이미 한도 초과 알림을 받았습니다.", user.getId());
                        alreadyNotifiedCount++;
                        continue;
                    }

                    // 일일 한도 설정이 있는지 확인
                    if (user.getDailyLimit() == null || user.getDailyLimit().compareTo(BigDecimal.ZERO) == 0) {
                        log.debug("사용자(ID: {})는 일일 한도가 설정되지 않았습니다.", user.getId());
                        continue;
                    }

                    int dailyLimit = user.getDailyLimit().intValue();
                    BigDecimal todaySpent = depositMapper.sumTodaySpendingByUserId(user.getId());

                    checkedCount++;

                    if (todaySpent.intValue() > dailyLimit) {
                        log.info("사용자(ID: {})의 한도 초과 감지: 한도={}원, 지출={}원",
                                user.getId(), dailyLimit, todaySpent.intValue());

                        // 이메일 발송 (비동기 처리)
                        notificationService.sendDailyLimitExceededEmail(user, dailyLimit, todaySpent.intValue());

                        // 알림 저장
                        Notification newNotification = Notification.builder()
                                .userId(user.getId())
                                .contents(String.format("일일 한도(%d원)를 초과했습니다. (오늘 지출:%d원)", dailyLimit, todaySpent.intValue()))
                                .type(NOTIFICATION_TYPE_LIMIT_EXCEEDED)
                                .isRead(false)
                                .createdAt(LocalDateTime.now())
                                .build();

                        notificationMapper.save(newNotification);
                        exceededCount++;
                    }

                } catch (Exception e) {
                    log.error("사용자(ID: {})의 일일 한도 체크 실패", user.getId(), e);
                    // 개별 사용자 실패해도 계속 진행
                }
            }

            log.info("전체 사용자 일일 한도 체크 완료: 체크={}명, 초과={}명, 이미알림={}명",
                    checkedCount, exceededCount, alreadyNotifiedCount);

        } catch (Exception e) {
            log.error("전체 사용자 일일 한도 체크 실패", e);
            throw new RuntimeException("일일 한도 체크 실패", e);
        }
    }
}

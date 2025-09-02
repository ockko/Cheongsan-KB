package cheongsan.domain.deposit.scheduler;

import cheongsan.domain.deposit.dto.BudgetLimitDTO;
import cheongsan.domain.deposit.service.BudgetService;
import cheongsan.domain.notification.entity.Notification;
import cheongsan.domain.notification.mapper.NotificationMapper;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class BudgetUpdateScheduler {
    private final UserMapper userMapper;
    private final BudgetService budgetService;
    private final NotificationMapper notificationMapper;

    // 매월 1일 새벽 1시에 실행
    @Scheduled(cron = "0 0 1 1 * ?")
    @Transactional
    public void updateUserBudgets() {
        log.info("월간 사용자 예산 한도 업데이트 스케줄러 시작...");

        List<User> allUsers = userMapper.findUsersWithConnectedId();
        log.info("총 {}명의 사용자를 대상으로 한도 검증을 시작합니다.", allUsers.size());

        for (User user : allUsers) {
            try {
                BudgetLimitDTO newLimits = budgetService.getBudgetLimits(user.getId());
                int newMaximumLimit = newLimits.getMaximumDailyLimit();
                int newRecommendedLimit = newLimits.getRecommendedDailyLimit();

                BigDecimal currentUserLimit = user.getDailyLimit();

                if (currentUserLimit != null && currentUserLimit.intValue() > newMaximumLimit) {
                    userMapper.updateDailyLimitOnly(user.getId(), new BigDecimal(newRecommendedLimit));

                    String message = String.format(
                            "지난달 재정 변동으로 인해 일일 소비 한도가 %,d원으로 자동 조정되었습니다.",
                            newRecommendedLimit
                    );
                    Notification newNotification = Notification.builder()
                            .userId(user.getId())
                            .type("BUDGET_ADJUSTED")
                            .contents(message)
                            .isRead(false)
                            .createdAt(LocalDateTime.now())
                            .build();
                    notificationMapper.save(newNotification);

                    log.info("사용자(ID: {})의 한도를 {} -> {} (으)로 자동 조정하고 알림을 생성했습니다.", user.getId(), currentUserLimit, newRecommendedLimit);
                }
            } catch (IllegalStateException e) {
                // 월 소득 정보가 없어 한도 계산에 실패한 경우 등
                log.warn("사용자(ID: {})의 한도를 계산할 수 없습니다: {}", user.getId(), e.getMessage());
            } catch (Exception e) {
                // 그 외 예기치 못한 에러
                log.error("사용자(ID: {})의 한도 업데이트 중 에러 발생", user.getId(), e);
            }
        }
        log.info("월간 사용자 예산 한도 업데이트 스케줄러 종료.");
    }
}

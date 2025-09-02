package cheongsan.domain.debt.scheduler;

import cheongsan.domain.debt.service.RepaymentUpdateService;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RepaymentUpdateScheduler {

    private final RepaymentUpdateService updateService;
    private final UserMapper userMapper;

    @Scheduled(cron = "0 0 18 * * ?")
    public void runDailyUpdate() {
        List<Long> userIds = userMapper.getAllUserIds(); // 모든 유저 조회
        for (Long userId : userIds) {
            updateService.updateNextPaymentDates(userId);
        }
    }
}


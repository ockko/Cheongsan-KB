package cheongsan.domain.notification.service;

import cheongsan.domain.deposit.entity.Transaction;
import cheongsan.domain.notification.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class TransactionTestService {
    private final TransactionMapper transactionMapper;
    private final NotificationService notificationService;

    @Transactional
    public void addTransaction(Transaction transaction) {
        transactionMapper.insertTransaction(transaction);

        // 거래 추가 후 소비한도 초과 여부 확인 + 알림 전송
        notificationService.checkAndNotifyIfOverLimit(transaction.getUserId());
    }


}

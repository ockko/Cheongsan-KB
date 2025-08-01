package cheongsan.domain.notification.service;

import cheongsan.domain.notification.entity.Transactions;
import cheongsan.domain.notification.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionMapper transactionMapper;
    private final NotificationService notificationService;

    @Transactional
    public void processTransaction(Transactions transaction) {
        transactionMapper.insertTransaction(transaction);
        log.info("거래내역 저장 완료 - 사용자ID: {}", transaction.getUserId());

        // 소비 한도 초과 확인 및 알림 전송
        notificationService.checkAndNotifyIfOverLimit(transaction.getUserId());
    }

}

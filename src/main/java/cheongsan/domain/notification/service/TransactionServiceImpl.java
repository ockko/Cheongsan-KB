package cheongsan.domain.notification.service;

import cheongsan.domain.deposit.service.LimitCheckService;
import cheongsan.domain.notification.entity.Transactions;
import cheongsan.domain.notification.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionMapper transactionMapper;
    private final NotificationService notificationService;
    private final LimitCheckService limitCheckService;

    @Transactional
    public ResponseEntity<String> processTransaction(Transactions transaction) {
        transactionMapper.insertTransaction(transaction);
        log.info("거래내역 저장 완료 - 사용자ID: {}", transaction.getUserId());

        // 소비 한도 초과 확인 및 알림 전송
        limitCheckService.checkDailyLimitExceeded(transaction.getUserId());

        return ResponseEntity.ok("거래 실행 및 알림 처리 완료");
    }
}

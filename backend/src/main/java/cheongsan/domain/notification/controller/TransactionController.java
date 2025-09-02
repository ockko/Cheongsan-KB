package cheongsan.domain.notification.controller;

import cheongsan.domain.notification.entity.Transactions;
import cheongsan.domain.notification.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cheongsan/notifications")
@RequiredArgsConstructor
@Log4j2
public class TransactionController {
    private final TransactionService transactionTestService;

    /**
     * 지출 실행 및 소비한도 초과 알림 체크 API
     */
    @PostMapping("/transactions")
    public ResponseEntity<String> processTransaction(@RequestBody Transactions transaction) {
        try {
            transactionTestService.processTransaction(transaction);
            log.info("지출 거래 실행 및 알림 처리 완료!");
            return ResponseEntity.ok("지출 거래 실행 및 알림 처리 완료!");
        } catch (Exception e) {
            log.error("지출 내역 추가 실패", e);
            return ResponseEntity.internalServerError().body("지출 내역 추가 중 오류 발생");
        }
    }
}

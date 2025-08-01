package cheongsan.domain.notification.service;

import cheongsan.domain.notification.entity.Transactions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface TransactionService {
    ResponseEntity<String> processTransaction(@RequestBody Transactions transaction);
}

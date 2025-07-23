package cheongsan.domain.spending.service;

import cheongsan.domain.spending.dto.TransactionDTO;
import cheongsan.domain.spending.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j2
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private static final Set<String> SALARY_KEYWORDS = new HashSet<>(Arrays.asList(
            "급여", "월급", "급료", "상여", "보너스"
    ));

    private static final Set<String> FIXED_WITHDRAW_KEYWORDS = new HashSet<>(Arrays.asList(
            "자동이체", "지로납부"
    ));

    @Override
    public BigDecimal calculateRegularMonthlyTransfer(Long userId, int year, int month) {
        List<TransactionDTO> transferTransactions = transactionRepository.findTransferTransactionsByMonth(userId, year, month);

        return transferTransactions.stream()
                .filter(this::isSalary)
                .map(TransactionDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateMonthlyFixedWithdraw(Long userId, int year, int month) {
        List<TransactionDTO> withdrawTransactions = transactionRepository.findWithdrawTransactionsByMonth(userId, year, month);

        return withdrawTransactions.stream()
                .filter(this::isFixedWithdraw)
                .map(TransactionDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isSalary(TransactionDTO transaction) {
        String desc1 = transaction.getResAccountDesc1();
        String desc2 = transaction.getResAccountDesc2();
        String desc3 = transaction.getResAccountDesc3();

        return SALARY_KEYWORDS.stream().anyMatch(keyword ->
                (desc1 != null && desc1.contains(keyword)) ||
                        (desc2 != null && desc2.contains(keyword)) ||
                        (desc3 != null && desc3.contains(keyword))
        );
    }

    private boolean isFixedWithdraw(TransactionDTO transaction) {
        String desc1 = transaction.getResAccountDesc1();
        String desc2 = transaction.getResAccountDesc2();
        String desc3 = transaction.getResAccountDesc3();

        return FIXED_WITHDRAW_KEYWORDS.stream().anyMatch(keyword ->
                (desc1 != null && desc1.contains(keyword)) ||
                        (desc2 != null && desc2.contains(keyword)) ||
                        (desc3 != null && desc3.contains(keyword))
        );
    }
}

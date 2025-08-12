package cheongsan.domain.deposit.service;

import cheongsan.domain.deposit.dto.DailyTransactionDTO;
import cheongsan.domain.deposit.dto.MonthlyTransactionDTO;
import cheongsan.domain.deposit.entity.Transaction;
import cheongsan.domain.deposit.mapper.DepositMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {
    private final DepositMapper depositMapper;

    private static final Set<String> SALARY_KEYWORDS = new HashSet<>(Arrays.asList(
            "급여", "월급", "급료", "상여", "보너스"
    ));

    private static final Set<String> FIXED_WITHDRAW_KEYWORDS = new HashSet<>(Arrays.asList(
            "자동이체", "지로납부"
    ));

    @Override
    public List<MonthlyTransactionDTO> getMonthlyTransactions(Long userId, int year, int month) {
        log.info("월별 거래 내역 조회 - userId: {}, year: {}, month: {}", userId, year, month);

        List<MonthlyTransactionDTO> monthlyTransactions = depositMapper.getMonthlyTransactions(userId, year, month);

        log.info("조회된 월별 거래 내역 수: {}", monthlyTransactions.size());
        return monthlyTransactions;
    }

    @Override
    public List<DailyTransactionDTO> getDailyTransactions(Long userId, LocalDate date) {
        log.info("일별 거래 내역 조회 - userId: {}, date: {}", userId, date);

        List<Transaction> transactions = depositMapper.getDailyTransactions(userId, date);

        List<DailyTransactionDTO> result = transactions.stream()
                .map(transaction -> {
                    DailyTransactionDTO dto = DailyTransactionDTO.of(transaction);
                    // 추가 정보 설정 (계좌번호)
                    dto.setAccountNumber(transaction.getAccountNumber());
                    return dto;
                })
                .collect(Collectors.toList());

        log.info("조회된 일별 거래 내역 수: {}", result.size());
        for (DailyTransactionDTO dto : result) {
            log.info(dto.getResAccountDesc3());
        }
        return result;
    }

    @Override
    public BigDecimal calculateRegularMonthlyTransfer(Long userId, int year, int month) {
        List<Transaction> transferTransactions = depositMapper.findTransferTransactionsByMonth(userId, year, month);

        return transferTransactions.stream()
                .filter(this::isSalary)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateMonthlyFixedWithdraw(Long userId, int year, int month) {
        List<Transaction> withdrawTransactions = depositMapper.findWithdrawTransactionsByMonth(userId, year, month);

        return withdrawTransactions.stream()
                .filter(this::isFixedWithdraw)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isSalary(Transaction transaction) {
        String desc1 = transaction.getResAccountDesc1();
        String desc2 = transaction.getResAccountDesc2();
        String desc3 = transaction.getResAccountDesc3();

        return SALARY_KEYWORDS.stream().anyMatch(keyword ->
                (desc1 != null && desc1.contains(keyword)) ||
                        (desc2 != null && desc2.contains(keyword)) ||
                        (desc3 != null && desc3.contains(keyword))
        );
    }

    private boolean isFixedWithdraw(Transaction transaction) {
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

package cheongsan.domain.deposit.service;

import cheongsan.common.constant.ResponseMessage;
import cheongsan.domain.deposit.dto.DailySpendingDTO;
import cheongsan.domain.deposit.dto.DailyTransactionDTO;
import cheongsan.domain.deposit.dto.MonthlyTransactionDTO;
import cheongsan.domain.deposit.entity.Transaction;
import cheongsan.domain.deposit.mapper.DepositMapper;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
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

    private final UserMapper userMapper;
    private final DepositMapper depositMapper;

    private static final Set<String> SALARY_KEYWORDS = new HashSet<>(Arrays.asList(
            "급여", "월급", "급료", "상여", "보너스"
    ));

    private static final Set<String> FIXED_WITHDRAW_KEYWORDS = new HashSet<>(Arrays.asList(
            "자동이체", "지로납부"
    ));

    @Override
    public List<MonthlyTransactionDTO> getMonthlyTransactions(int year, int month) {
        log.info("월별 거래 내역 조회 - year: {}, month: {}", year, month);

        // TODO: 현재는 하드코딩된 사용자 ID 사용, 실제로는 SecurityContext에서 가져와야 함
        Long userId = 1L;

        List<MonthlyTransactionDTO> monthlyTransactions = depositMapper.getMonthlyTransactions(userId, year, month);

        log.info("조회된 월별 거래 내역 수: {}", monthlyTransactions.size());
        return monthlyTransactions;
    }

    @Override
    public List<DailyTransactionDTO> getDailyTransactions(LocalDate date) {
        log.info("일별 거래 내역 조회 - date: {}", date);

        // TODO: 현재는 하드코딩된 사용자 ID 사용, 실제로는 SecurityContext에서 가져와야 함
        Long userId = 1L;

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

    @Override
    public DailySpendingDTO getDailySpendingStatus(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException(ResponseMessage.USER_NOT_FOUND.getMessage());
        }
        int dailyLimit = (user.getDailyLimit() != null) ? user.getDailyLimit().intValue() : 0;

        BigDecimal todaySpent = depositMapper.sumTodaySpendingByUserId(userId);

        return new DailySpendingDTO(dailyLimit, todaySpent.intValue());
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

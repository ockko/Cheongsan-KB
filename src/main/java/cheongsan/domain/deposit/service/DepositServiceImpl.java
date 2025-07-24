package cheongsan.domain.deposit.service;

import cheongsan.domain.deposit.dto.DailyTransactionDTO;
import cheongsan.domain.deposit.dto.MonthlyTransactionDTO;
import cheongsan.domain.deposit.entity.Transaction;
import cheongsan.domain.deposit.mapper.DepositMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {
    private final DepositMapper depositMapper;

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
}

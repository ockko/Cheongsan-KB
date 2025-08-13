package cheongsan.domain.codef.service;

import cheongsan.domain.codef.dto.LoanTransactionResponseDTO;
import cheongsan.domain.codef.dto.TransactionListResponseDTO;
import cheongsan.domain.debt.entity.DebtAccount;
import cheongsan.domain.debt.entity.DebtTransaction;
import cheongsan.domain.debt.mapper.DebtMapper;
import cheongsan.domain.debt.mapper.DebtTransactionMapper;
import cheongsan.domain.deposit.entity.DepositAccount;
import cheongsan.domain.deposit.entity.Transaction;
import cheongsan.domain.deposit.mapper.DepositAccountMapper;
import cheongsan.domain.deposit.mapper.DepositMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Log4j2
@Service
@RequiredArgsConstructor
public class CodefTransactionSyncServiceImpl implements CodefTransactionSyncService {
    private final CodefService codefService;
    private final DepositMapper depositMapper;
    private final DepositAccountMapper depositAccountMapper;
    private final DebtMapper debtMapper;
    private final DebtTransactionMapper debtTransactionMapper;
    private final CodefUtilServiceImpl utilService;

    /**
     * 예금 거래내역 동기화
     */
    public void syncDepositTransactionHistory(Long userId, String connectedId,
                                              String organizationCode, String accountNumber) {
        try {
            // 최근 3개월 조회
            String endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String startDate = LocalDate.now().minusMonths(3).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            log.info("사용자 {}의 거래내역 동기화 시작: account={}", userId, accountNumber);

            TransactionListResponseDTO transactionList = codefService.getTransactionListWithDates(
                    connectedId, organizationCode, accountNumber, startDate, endDate);

            if (transactionList.getData() == null || transactionList.getData().getResTrHistoryList() == null) {
                log.warn("사용자 {}의 거래내역이 없습니다: account={}", userId, accountNumber);
                return;
            }

            DepositAccount depositAccount = depositAccountMapper.findByUserIdAndAccountNumber(userId, accountNumber);
            if (depositAccount == null) {
                log.warn("사용자 {}의 예금계좌 정보를 찾을 수 없습니다: {}", userId, accountNumber);
                return;
            }

            int savedCount = 0;
            int duplicateCount = 0;

            for (TransactionListResponseDTO.TransactionDetail transaction : transactionList.getData().getResTrHistoryList()) {
                try {
                    if (processDepositTransaction(userId, depositAccount, transaction)) {
                        savedCount++;
                    } else {
                        duplicateCount++;
                    }
                } catch (Exception e) {
                    log.error("사용자 {}의 개별 거래내역 저장 실패", userId, e);
                }
            }

            log.info("사용자 {}의 거래내역 동기화 완료: account={} -> {}건 저장, {}건 중복 스킵",
                    userId, accountNumber, savedCount, duplicateCount);

        } catch (RuntimeException e) {
            // CODEF API 에러 처리 개선
            if (e.getMessage() != null && e.getMessage().contains("CF-12040")) {
                log.warn("사용자 {}의 계좌 {}는 거래내역 조회가 지원되지 않는 상품입니다. 스킵합니다.",
                        userId, accountNumber);
                return;
            }
            log.error("사용자 {}의 거래내역 동기화 실패: account={}", userId, accountNumber, e);
        } catch (Exception e) {
            log.error("사용자 {}의 거래내역 동기화 실패: account={}", userId, accountNumber, e);
        }
    }

    /**
     * 개별 예금 거래내역 처리
     */
    private boolean processDepositTransaction(Long userId, DepositAccount depositAccount,
                                              TransactionListResponseDTO.TransactionDetail transaction) {
        LocalDateTime transactionTime = utilService.parseTransactionDateTime(
                transaction.getResAccountTrDate(), transaction.getResAccountTrTime());

        BigDecimal amount = BigDecimal.ZERO;
        String type = "";

        if (transaction.getResAccountIn() != null && !transaction.getResAccountIn().equals("0")) {
            amount = new BigDecimal(transaction.getResAccountIn());
            type = "TRANSFER";
        } else if (transaction.getResAccountOut() != null && !transaction.getResAccountOut().equals("0")) {
            amount = new BigDecimal(transaction.getResAccountOut());
            type = "WITHDRAW";
        } else {
            return false;
        }

        // 중복 체크
        if (depositMapper.isTransactionExists(userId, transactionTime, amount, type)) {
            log.debug("사용자 {}의 중복 거래내역 스킵: time={}, amount={}, type={}",
                    userId, transactionTime, amount, type);
            return false;
        }

        // 거래내역 저장
        Transaction transactionEntity = Transaction.builder()
                .depositAccountId(depositAccount.getId())
                .userId(userId)
                .transactionTime(transactionTime)
                .afterBalance(new BigDecimal(transaction.getResAfterTranBalance() != null ? transaction.getResAfterTranBalance() : "0"))
                .amount(amount)
                .type(type)
                .resAccountDesc1(transaction.getResAccountDesc1())
                .resAccountDesc2(transaction.getResAccountDesc2())
                .resAccountDesc3(transaction.getResAccountDesc3())
                .build();

        depositMapper.insertTransaction(transactionEntity);
        log.debug("사용자 {}의 거래내역 저장: time={}, amount={}, type={}", userId, transactionTime, amount, type);
        return true;
    }

    /**
     * 대출 거래내역 동기화
     */
    public void syncLoanTransactionHistory(Long userId, String connectedId,
                                           String organizationCode, String accountNumber) {
        try {
            String endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            log.info("사용자 {}의 대출 거래내역 동기화 시작: account={}", userId, accountNumber);

            LoanTransactionResponseDTO loanTransactionList = codefService.getLoanTransactionList(
                    connectedId, organizationCode, accountNumber, startDate, endDate);

            if (loanTransactionList.getData() == null || loanTransactionList.getData().getResTrHistoryList() == null) {
                log.warn("사용자 {}의 대출 거래내역이 없습니다: account={}", userId, accountNumber);
                return;
            }

            DebtAccount debtAccount = debtMapper.findByUserIdAndAccount(userId, accountNumber);
            if (debtAccount == null) {
                log.warn("사용자 {}의 대출계좌 정보를 찾을 수 없습니다: {}", userId, accountNumber);
                return;
            }

            int savedCount = 0;

            for (LoanTransactionResponseDTO.LoanTransactionDetail transaction : loanTransactionList.getData().getResTrHistoryList()) {
                try {
                    if (processLoanTransaction(userId, debtAccount, transaction)) {
                        savedCount++;
                    }
                } catch (Exception e) {
                    log.error("사용자 {}의 개별 대출 거래내역 저장 실패", userId, e);
                }
            }

            log.info("사용자 {}의 대출 거래내역 동기화 완료: account={} -> {}건 저장", userId, accountNumber, savedCount);

        } catch (Exception e) {
            log.error("사용자 {}의 대출 거래내역 동기화 실패: account={}", userId, accountNumber, e);
        }
    }

    /**
     * 개별 대출 거래내역 처리
     */
    private boolean processLoanTransaction(Long userId, DebtAccount debtAccount,
                                           LoanTransactionResponseDTO.LoanTransactionDetail transaction) {
        LocalDate transactionDate = utilService.parseDate(transaction.getTransactionDate());

        // 중복 체크
        if (debtTransactionMapper.isDebtTransactionExists(debtAccount.getId(), transactionDate)) {
            log.debug("사용자 {}의 중복 대출 거래내역 스킵: date={}", userId, transactionDate);
            return false;
        }

        // 대출 거래내역 저장
        DebtTransaction debtTransaction = DebtTransaction.builder()
                .debtAccountId(debtAccount.getId())
                .transactionDate(transactionDate)
                .principalAmount(new BigDecimal(transaction.getPrincipalAmount() != null ? transaction.getPrincipalAmount() : "0"))
                .interestAmount(new BigDecimal(transaction.getInterestAmount() != null ? transaction.getInterestAmount() : "0"))
                .remainingBalance(new BigDecimal(transaction.getRemainingBalance() != null ? transaction.getRemainingBalance() : "0"))
                .createdAt(LocalDateTime.now())
                .build();

        debtTransactionMapper.insertDebtTransaction(debtTransaction);
        log.debug("사용자 {}의 대출 거래내역 저장: date={}", userId, transactionDate);
        return true;
    }
}

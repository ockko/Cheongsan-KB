package cheongsan.domain.codef.service;

import cheongsan.domain.codef.dto.AccountListResponseDTO;
import cheongsan.domain.debt.entity.DebtAccount;
import cheongsan.domain.debt.mapper.DebtMapper;
import cheongsan.domain.deposit.entity.DepositAccount;
import cheongsan.domain.deposit.mapper.DepositAccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class CodefAccountSyncServiceImpl implements CodefAccountSyncService {
    private final DebtMapper debtMapper;
    private final DepositAccountMapper depositAccountMapper;
    private final CodefTransactionSyncServiceImpl transactionSyncService;
    private final CodefUtilServiceImpl utilService;

    /**
     * 예금계좌 동기화
     */
    public void syncDepositAccounts(Long userId, String organizationCode,
                                    List<AccountListResponseDTO.DepositAccount> depositAccounts,
                                    String connectedId) {
        for (AccountListResponseDTO.DepositAccount account : depositAccounts) {
            try {
                boolean isExistingAccount = depositAccountMapper.isDepositAccountExists(userId, account.getResAccount());

                if (isExistingAccount) {
                    log.info("사용자 {}의 이미 존재하는 예금계좌: {}", userId, account.getResAccount());
                    updateExistingDepositAccount(userId, account, connectedId, organizationCode);
                } else {
                    log.info("사용자 {}의 새로운 예금계좌 저장: {}", userId, account.getResAccount());
                    createNewDepositAccount(userId, organizationCode, account, connectedId);
                }

            } catch (Exception e) {
                log.error("사용자 {}의 예금계좌 동기화 실패: account={}", userId, account.getResAccount(), e);
            }
        }
    }

    /**
     * 기존 예금계좌 업데이트
     */
    private void updateExistingDepositAccount(Long userId, AccountListResponseDTO.DepositAccount account,
                                              String connectedId, String organizationCode) {
        // 잔액만 업데이트
        DepositAccount existingAccount = depositAccountMapper.findByUserIdAndAccountNumber(userId, account.getResAccount());
        if (existingAccount != null) {
            BigDecimal newBalance = new BigDecimal(account.getResAccountBalance() != null ? account.getResAccountBalance() : "0");
            depositAccountMapper.updateBalance(existingAccount.getId(), newBalance);
            log.info("사용자 {}의 계좌 {} 잔액 업데이트: {}", userId, account.getResAccount(), newBalance);
        }

        // 거래내역 동기화
        transactionSyncService.syncDepositTransactionHistory(userId, connectedId, organizationCode, account.getResAccount());
    }

    /**
     * 새로운 예금계좌 생성
     */
    private void createNewDepositAccount(Long userId, String organizationCode,
                                         AccountListResponseDTO.DepositAccount account, String connectedId) {
        DepositAccount depositAccount = DepositAccount.builder()
                .userId(userId)
                .organizationCode(organizationCode)
                .accountNumber(account.getResAccount())
                .currentBalance(new BigDecimal(account.getResAccountBalance() != null ? account.getResAccountBalance() : "0"))
                .build();

        depositAccountMapper.insertDepositAccount(depositAccount);
        log.info("사용자 {}의 새로운 예금계좌 저장: {}", userId, account.getResAccount());

        // 새로운 계좌의 거래내역 동기화
        transactionSyncService.syncDepositTransactionHistory(userId, connectedId, organizationCode, account.getResAccount());
    }

    /**
     * 대출계좌 동기화
     */
    public void syncLoanAccounts(Long userId, String organizationCode,
                                 List<AccountListResponseDTO.LoanAccount> loanAccounts, String connectedId) {
        for (AccountListResponseDTO.LoanAccount account : loanAccounts) {
            try {
                Long orgCodeLong = utilService.getOrCreateFinancialInstitution(organizationCode);
                boolean isExistingAccount = debtMapper.isDebtAccountExists(userId, account.getResAccount());

                log.info("🔍 대출계좌 처리: userId={}, account={}, isExisting={}",
                        userId, account.getResAccount(), isExistingAccount);

                if (isExistingAccount) {
                    log.info("사용자 {}의 이미 존재하는 대출계좌: {}", userId, account.getResAccount());

                    // 현재 DB 잔액 조회
                    DebtAccount existingAccount = debtMapper.findByUserIdAndAccount(userId, account.getResAccount());
                    BigDecimal currentDbBalance = existingAccount != null ? existingAccount.getCurrentBalance() : BigDecimal.ZERO;
                    BigDecimal newApiBalance = new BigDecimal(account.getResAccountBalance() != null ? account.getResAccountBalance() : "0");

                    log.info("🔍 잔액 비교: DB잔액={}, API잔액={}", currentDbBalance, newApiBalance);

                    // 잔액 업데이트
                    if (existingAccount != null && currentDbBalance.compareTo(newApiBalance) != 0) {
                        debtMapper.updateDebtBalance(existingAccount.getId(), newApiBalance);
                        log.info("✅ 대출계좌 잔액 업데이트 완료: {} -> {}", currentDbBalance, newApiBalance);
                    } else {
                        log.info("ℹ️ 대출계좌 잔액 변경 없음: {}", currentDbBalance);
                    }

                    // 거래내역 동기화
                    transactionSyncService.syncLoanTransactionHistory(userId, connectedId, organizationCode, account.getResAccount());
                } else {
                    log.info("사용자 {}의 새로운 대출계좌 저장: {}", userId, account.getResAccount());
                    createNewLoanAccount(userId, orgCodeLong, account, connectedId, organizationCode);
                }

            } catch (Exception e) {
                log.error("사용자 {}의 대출계좌 동기화 실패: account={}", userId, account.getResAccount(), e);
            }
        }
    }

    /**
     * 새로운 대출계좌 생성
     */
    private void createNewLoanAccount(Long userId, Long organizationCode,
                                      AccountListResponseDTO.LoanAccount account,
                                      String connectedId, String orgCode) {
        DebtAccount debtAccount = utilService.convertToDebtAccount(userId, organizationCode, account);
        debtMapper.insertDebt(debtAccount);
        log.info("사용자 {}의 새로운 대출계좌 저장: {}", userId, account.getResAccount());

        // 새로운 대출계좌의 거래내역 동기화
        transactionSyncService.syncLoanTransactionHistory(userId, connectedId, orgCode, account.getResAccount());
    }
}

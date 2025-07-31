package cheongsan.domain.codef.service;

import cheongsan.domain.codef.dto.AccountListResponseDTO;
import cheongsan.domain.codef.dto.LoanTransactionResponseDTO;
import cheongsan.domain.codef.dto.TransactionListResponseDTO;
import cheongsan.domain.debt.entity.DebtAccount;
import cheongsan.domain.debt.entity.DebtTransaction;
import cheongsan.domain.debt.mapper.DebtMapper;
import cheongsan.domain.debt.mapper.DebtTransactionMapper;
import cheongsan.domain.debt.mapper.FinancialInstitutionMapper;
import cheongsan.domain.deposit.entity.DepositAccount;
import cheongsan.domain.deposit.entity.Transaction;
import cheongsan.domain.deposit.mapper.DepositAccountMapper;
import cheongsan.domain.deposit.mapper.DepositMapper;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class CodefSyncService {
    private final CodefService codefService;
    private final UserMapper userMapper;
    private final DebtMapper debtMapper;
    private final DepositMapper depositMapper;
    private final DepositAccountMapper depositAccountMapper;
    private final FinancialInstitutionMapper financialInstitutionMapper;
    private final DebtTransactionMapper debtTransactionMapper;

    /**
     * 사용자 계좌 정보 전체 동기화
     */
    @Transactional
    public void syncUserAccountData(Long userId) {
        try {
            User user = userMapper.findById(userId);
            if (user == null) {
                throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
            }

            String connectedId = user.getConnectedId();
            if (connectedId == null || connectedId.isEmpty()) {
                throw new IllegalArgumentException("Connected ID가 없습니다. 먼저 계좌 연동을 해주세요.");
            }

            // 금융기관별로 계좌 정보 조회
            List<String> organizationCodes = Arrays.asList("0081"); // 하나은행 (필요시 추가 가능)

            for (String orgCode : organizationCodes) {
                syncAccountDataForOrganization(userId, connectedId, orgCode);
            }

        } catch (Exception e) {
            log.error("사용자 계좌 데이터 동기화 실패: userId={}", userId, e);
            throw new RuntimeException("계좌 데이터 동기화 실패", e);
        }
    }

    /**
     * 기관별 계좌 데이터 동기화
     */
    private void syncAccountDataForOrganization(Long userId, String connectedId, String organizationCode) {
        try {
            AccountListResponseDTO accountList = codefService.getAccountList(connectedId, organizationCode);

            if (accountList.getData() == null) {
                log.warn("계좌 정보가 없습니다. userId={}, orgCode={}", userId, organizationCode);
                return;
            }

            // 예금계좌 동기화
            if (accountList.getData().getResDepositTrust() != null) {
                syncDepositAccounts(userId, organizationCode, accountList.getData().getResDepositTrust(), connectedId);
            }

            // 대출계좌 동기화
            if (accountList.getData().getResLoan() != null) {
                syncLoanAccounts(userId, organizationCode, accountList.getData().getResLoan(), connectedId);
            }

        } catch (Exception e) {
            log.error("기관별 계좌 데이터 동기화 실패: userId={}, orgCode={}", userId, organizationCode, e);
        }
    }

    /**
     * 예금계좌 동기화 - 사용자별 격리 강화
     */
    private void syncDepositAccounts(Long userId, String organizationCode,
                                     List<AccountListResponseDTO.DepositAccount> depositAccounts, String connectedId) {
        for (AccountListResponseDTO.DepositAccount account : depositAccounts) {
            try {
                // 해당 사용자만 체크하도록 강화
                if (depositAccountMapper.isDepositAccountExists(userId, account.getResAccount())) {
                    log.info("사용자 {}의 이미 존재하는 예금계좌: {}", userId, account.getResAccount());

                    // 잔액만 업데이트 (해당 사용자의 계좌만)
                    DepositAccount existingAccount = depositAccountMapper.findByUserIdAndAccountNumber(userId, account.getResAccount());
                    if (existingAccount != null) {
                        BigDecimal newBalance = new BigDecimal(account.getResAccountBalance() != null ? account.getResAccountBalance() : "0");
                        depositAccountMapper.updateBalance(existingAccount.getId(), newBalance);
                        log.info("사용자 {}의 계좌 {} 잔액 업데이트: {}", userId, account.getResAccount(), newBalance);
                    }
                    continue;
                }

                // 새로운 예금계좌 저장 (사용자 ID 명시)
                DepositAccount depositAccount = DepositAccount.builder()
                        .userId(userId)  // 명시적으로 사용자 ID 설정
                        .organizationCode(organizationCode)
                        .accountNumber(account.getResAccount())
                        .currentBalance(new BigDecimal(account.getResAccountBalance() != null ? account.getResAccountBalance() : "0"))
                        .build();

                depositAccountMapper.insertDepositAccount(depositAccount);
                log.info("사용자 {}의 새로운 예금계좌 저장: {}", userId, account.getResAccount());

                // 해당 사용자의 거래내역만 동기화
                syncTransactionHistoryForUser(userId, connectedId, organizationCode, account.getResAccount());

            } catch (Exception e) {
                log.error("사용자 {}의 예금계좌 동기화 실패: account={}", userId, account.getResAccount(), e);
            }
        }
    }

    /**
     * 거래내역 동기화 - 사용자별 격리 강화
     */
    private void syncTransactionHistoryForUser(Long userId, String connectedId, String organizationCode, String accountNumber) {
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

            // 해당 사용자의 예금계좌 정보만 조회
            DepositAccount depositAccount = depositAccountMapper.findByUserIdAndAccountNumber(userId, accountNumber);
            if (depositAccount == null) {
                log.warn("사용자 {}의 예금계좌 정보를 찾을 수 없습니다: {}", userId, accountNumber);
                return;
            }

            int savedCount = 0;
            int duplicateCount = 0;

            for (TransactionListResponseDTO.TransactionDetail transaction : transactionList.getData().getResTrHistoryList()) {
                try {
                    LocalDateTime transactionTime = parseTransactionDateTime(
                            transaction.getResAccountTrDate(),
                            transaction.getResAccountTrTime()
                    );

                    BigDecimal amount = BigDecimal.ZERO;
                    String type = "";

                    if (transaction.getResAccountIn() != null && !transaction.getResAccountIn().equals("0")) {
                        amount = new BigDecimal(transaction.getResAccountIn());
                        type = "TRANSFER";
                    } else if (transaction.getResAccountOut() != null && !transaction.getResAccountOut().equals("0")) {
                        amount = new BigDecimal(transaction.getResAccountOut());
                        type = "WITHDRAW";
                    } else {
                        continue;
                    }

                    // 해당 사용자에 대해서만 중복 체크
                    if (depositMapper.isTransactionExists(userId, transactionTime, amount, type)) {
                        duplicateCount++;
                        log.debug("사용자 {}의 중복 거래내역 스킵: time={}, amount={}, type={}",
                                userId, transactionTime, amount, type);
                        continue;
                    }

                    // 거래내역 저장 (사용자 ID 명시)
                    Transaction transactionEntity = Transaction.builder()
                            .depositAccountId(depositAccount.getId())
                            .userId(userId)  // 명시적으로 사용자 ID 설정
                            .transactionTime(transactionTime)
                            .afterBalance(new BigDecimal(transaction.getResAfterTranBalance() != null ? transaction.getResAfterTranBalance() : "0"))
                            .amount(amount)
                            .type(type)
                            .resAccountDesc1(transaction.getResAccountDesc1())
                            .resAccountDesc2(transaction.getResAccountDesc2())
                            .resAccountDesc3(transaction.getResAccountDesc3())
                            .build();

                    depositMapper.insertTransaction(transactionEntity);
                    savedCount++;

                    log.debug("사용자 {}의 거래내역 저장: time={}, amount={}, type={}",
                            userId, transactionTime, amount, type);

                } catch (Exception e) {
                    log.error("사용자 {}의 개별 거래내역 저장 실패", userId, e);
                }
            }

            log.info("사용자 {}의 거래내역 동기화 완료: account={} -> {}건 저장, {}건 중복 스킵",
                    userId, accountNumber, savedCount, duplicateCount);

        } catch (Exception e) {
            log.error("사용자 {}의 거래내역 동기화 실패: account={}", userId, accountNumber, e);
        }
    }

    /**
     * 대출계좌 동기화 - 사용자별 격리 강화
     */
    private void syncLoanAccounts(Long userId, String organizationCode,
                                  List<AccountListResponseDTO.LoanAccount> loanAccounts, String connectedId) {
        for (AccountListResponseDTO.LoanAccount account : loanAccounts) {
            try {
                Long orgCodeLong = getOrCreateFinancialInstitution(organizationCode);

                // 해당 사용자만 체크하도록 강화
                if (debtMapper.isDebtAccountExists(userId, account.getResAccount())) {
                    log.info("사용자 {}의 이미 존재하는 대출계좌: {}", userId, account.getResAccount());
                    continue;
                }

                // 대출계좌 정보 저장 (사용자 ID 명시)
                DebtAccount debtAccount = convertToDebtAccount(userId, orgCodeLong, account);
                debtMapper.insertDebt(debtAccount);
                log.info("사용자 {}의 새로운 대출계좌 저장: {}", userId, account.getResAccount());

                // 해당 사용자의 대출 거래내역만 동기화
                syncLoanTransactionHistoryForUser(userId, connectedId, organizationCode, account.getResAccount());

            } catch (Exception e) {
                log.error("사용자 {}의 대출계좌 동기화 실패: account={}", userId, account.getResAccount(), e);
            }
        }
    }

    /**
     * 대출 거래내역 동기화 - 사용자별 격리 강화
     */
    private void syncLoanTransactionHistoryForUser(Long userId, String connectedId, String organizationCode, String accountNumber) {
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

            // 해당 사용자의 대출계좌 정보만 조회
            DebtAccount debtAccount = debtMapper.findByUserIdAndAccount(userId, accountNumber);
            if (debtAccount == null) {
                log.warn("사용자 {}의 대출계좌 정보를 찾을 수 없습니다: {}", userId, accountNumber);
                return;
            }

            int savedCount = 0;

            for (LoanTransactionResponseDTO.LoanTransactionDetail transaction : loanTransactionList.getData().getResTrHistoryList()) {
                try {
                    LocalDate transactionDate = parseDate(transaction.getTransactionDate());

                    // 해당 사용자의 대출계좌에 대해서만 중복 체크
                    if (debtTransactionMapper.isDebtTransactionExists(debtAccount.getId(), transactionDate)) {
                        log.debug("사용자 {}의 중복 대출 거래내역 스킵: date={}", userId, transactionDate);
                        continue;
                    }

                    // 대출 거래내역 저장
                    DebtTransaction debtTransaction = DebtTransaction.builder()
                            .debtAccountId(debtAccount.getId())  // 해당 사용자의 대출계좌 ID
                            .transactionDate(transactionDate)
                            .principalAmount(new BigDecimal(transaction.getPrincipalAmount() != null ? transaction.getPrincipalAmount() : "0"))
                            .interestAmount(new BigDecimal(transaction.getInterestAmount() != null ? transaction.getInterestAmount() : "0"))
                            .remainingBalance(new BigDecimal(transaction.getRemainingBalance() != null ? transaction.getRemainingBalance() : "0"))
                            .createdAt(LocalDateTime.now())
                            .build();

                    debtTransactionMapper.insertDebtTransaction(debtTransaction);
                    savedCount++;

                    log.debug("사용자 {}의 대출 거래내역 저장: date={}", userId, transactionDate);

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
     * 계좌 연동 상태 체크
     */
    public boolean isAccountLinked(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return false;
        }
        return user.getConnectedId() != null && !user.getConnectedId().isEmpty();
    }

    /**
     * 전체 사용자 계좌 데이터 배치 동기화
     */
    @Transactional
    public void batchSyncAllUsers() {
        try {
            List<User> usersWithConnectedId = userMapper.findUsersWithConnectedId();

            for (User user : usersWithConnectedId) {
                try {
                    syncUserAccountData(user.getId());
                    log.info("사용자 {}의 계좌 데이터 동기화 완료", user.getId());
                } catch (Exception e) {
                    log.error("사용자 {}의 계좌 데이터 동기화 실패", user.getId(), e);
                    // 개별 사용자 실패해도 계속 진행
                }
            }

        } catch (Exception e) {
            log.error("전체 사용자 계좌 데이터 배치 동기화 실패", e);
            throw new RuntimeException("배치 동기화 실패", e);
        }
    }

    // ===== 유틸리티 메서드들 =====

    private Long getOrCreateFinancialInstitution(String organizationCode) {
        String organizationName = getOrganizationName(organizationCode);
        Long orgCodeLong = financialInstitutionMapper.findCodeByName(organizationName);

        if (orgCodeLong == null) {
            financialInstitutionMapper.insertInstitution(organizationName);
            orgCodeLong = financialInstitutionMapper.findCodeByName(organizationName);
        }
        return orgCodeLong;
    }

    private String getOrganizationName(String organizationCode) {
        Map<String, String> codeToNameMap = new HashMap<>();
        codeToNameMap.put("0081", "하나은행");
        codeToNameMap.put("0020", "우리은행");
        codeToNameMap.put("0088", "신한은행");
        codeToNameMap.put("0004", "KB국민은행");
        codeToNameMap.put("0011", "농협은행");

        return codeToNameMap.getOrDefault(organizationCode, "기타금융기관");
    }

    private DebtAccount convertToDebtAccount(Long userId, Long organizationCode, AccountListResponseDTO.LoanAccount account) {
        return DebtAccount.builder()
                .userId(userId)
                .organizationCode(organizationCode)
                .resAccount(account.getResAccount())
                .debtName(account.getResAccountName() != null ? account.getResAccountName() : "대출")
                .currentBalance(new BigDecimal(account.getResAccountBalance() != null ? account.getResAccountBalance() : "0"))
                .originalAmount(new BigDecimal(account.getResAccountBalance() != null ? account.getResAccountBalance() : "0"))
                .interestRate(new BigDecimal("0.00"))
                .loanStartDate(parseDate(account.getResAccountStartDate()))
                .loanEndDate(parseDate(account.getResAccountEndDate()))
                .nextPaymentDate(LocalDate.now().plusMonths(1))
                .gracePeriodMonths(0L)
                .repaymentMethod("원리금균등상환")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}", dateString);
            return LocalDate.now();
        }
    }

    private LocalDateTime parseTransactionDateTime(String dateString, String timeString) {
        try {
            LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyyMMdd"));

            if (timeString != null && timeString.length() >= 6) {
                int hour = Integer.parseInt(timeString.substring(0, 2));
                int minute = Integer.parseInt(timeString.substring(2, 4));
                int second = Integer.parseInt(timeString.substring(4, 6));
                return date.atTime(hour, minute, second);
            } else {
                return date.atStartOfDay();
            }
        } catch (Exception e) {
            log.warn("거래 날짜/시간 파싱 실패: date={}, time={}", dateString, timeString);
            return LocalDateTime.now();
        }
    }
}

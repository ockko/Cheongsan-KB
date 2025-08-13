package cheongsan.domain.codef.service;

import cheongsan.domain.codef.dto.AccountListResponseDTO;
import cheongsan.domain.debt.entity.DebtAccount;
import cheongsan.domain.debt.mapper.DebtMapper;
import cheongsan.domain.deposit.entity.DepositAccount;
import cheongsan.domain.deposit.mapper.DepositAccountMapper;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 동기화 메인 서비스
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class CodefSyncService {
    private final CodefService codefService;
    private final UserMapper userMapper;
    private final DebtMapper debtMapper;
    private final DepositAccountMapper depositAccountMapper;
    private final CodefAccountSyncService accountSyncService;

    /**
     * 사용자 계좌 정보 전체 동기화 - 잔액 변경 감지 방식 (최적화)
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

            log.info("사용자 계좌 데이터 동기화 시작: userId={}", userId);

            List<String> organizationCodes = Arrays.asList("0081"); // 하나은행

            // 1단계: 계좌 목록 조회 + 잔액 변경 감지 (1회 API 호출)
            boolean hasChanges = false;
            Map<String, AccountListResponseDTO> accountListCache = new HashMap<>();

            for (String orgCode : organizationCodes) {
                AccountListResponseDTO accountList = codefService.getAccountList(connectedId, orgCode);
                accountListCache.put(orgCode, accountList);

                if (hasAccountBalanceChanged(userId, accountList)) {
                    hasChanges = true;
                    break;
                }
            }

            if (!hasChanges) {
                log.info("⏭️ 잔액 변경 없음으로 거래내역 동기화 스킵: userId={}", userId);
                return; // 1회 API 호출로 종료
            }

            // 2단계: 실제 거래내역 동기화 (캐시된 데이터 사용)
            log.info("🔄 잔액 변경 감지됨. 전체 동기화 시작: userId={}", userId);
            for (String orgCode : organizationCodes) {
                AccountListResponseDTO cachedAccountList = accountListCache.get(orgCode);
                syncAccountDataForOrganizationWithCache(userId, connectedId, orgCode, cachedAccountList);
            }

            log.info("✅ 사용자 계좌 데이터 동기화 완료: userId={}", userId);

        } catch (Exception e) {
            log.error("❌ 사용자 계좌 데이터 동기화 실패: userId={}", userId, e);
            throw new RuntimeException("계좌 데이터 동기화 실패", e);
        }
    }

    /**
     * 계좌 잔액 변경 감지 메서드
     */
    private boolean hasAccountBalanceChanged(Long userId, AccountListResponseDTO accountList) {
        try {
            log.info("잔액 변경 감지 시작: userId={}", userId);

            if (accountList.getData() == null) {
                log.warn("계좌 정보가 없습니다: userId={}", userId);
                return false;
            }

            boolean hasChanged = false;

            // 예금계좌 잔액 비교
            if (accountList.getData().getResDepositTrust() != null) {
                for (AccountListResponseDTO.DepositAccount account : accountList.getData().getResDepositTrust()) {
                    DepositAccount dbAccount = depositAccountMapper.findByUserIdAndAccountNumber(userId, account.getResAccount());
                    if (dbAccount != null) {
                        BigDecimal newBalance = new BigDecimal(account.getResAccountBalance() != null ? account.getResAccountBalance() : "0");
                        if (dbAccount.getCurrentBalance().compareTo(newBalance) != 0) {
                            log.info("예금계좌 잔액 변경 감지: userId={}, account={}, 기존={}, 신규={}",
                                    userId, account.getResAccount(), dbAccount.getCurrentBalance(), newBalance);
                            hasChanged = true;
                        }
                    } else {
                        log.info("새로운 예금계좌 발견: userId={}, account={}", userId, account.getResAccount());
                        hasChanged = true;
                    }
                }
            }

            // 대출계좌 잔액 비교
            if (accountList.getData().getResLoan() != null) {
                for (AccountListResponseDTO.LoanAccount account : accountList.getData().getResLoan()) {
                    DebtAccount dbAccount = debtMapper.findByUserIdAndAccount(userId, account.getResAccount());
                    if (dbAccount != null) {
                        BigDecimal newBalance = new BigDecimal(account.getResAccountBalance() != null ? account.getResAccountBalance() : "0");
                        if (dbAccount.getCurrentBalance().compareTo(newBalance) != 0) {
                            log.info("대출계좌 잔액 변경 감지: userId={}, account={}, 기존={}, 신규={}",
                                    userId, account.getResAccount(), dbAccount.getCurrentBalance(), newBalance);
                            hasChanged = true;
                        }
                    } else {
                        log.info("새로운 대출계좌 발견: userId={}, account={}", userId, account.getResAccount());
                        hasChanged = true;
                    }
                }
            }

            log.info("잔액 변경 감지 결과: userId={}, hasChanged={}", userId, hasChanged);
            return hasChanged;

        } catch (Exception e) {
            log.error("잔액 변경 감지 실패: userId={}", userId, e);
            return true; // 에러 시 안전하게 동기화 수행
        }
    }

    /**
     * 캐시된 데이터를 사용하는 기관별 계좌 데이터 동기화
     */
    private void syncAccountDataForOrganizationWithCache(Long userId, String connectedId,
                                                         String organizationCode,
                                                         AccountListResponseDTO accountList) {
        try {
            if (accountList.getData() == null) {
                log.warn("계좌 정보가 없습니다: userId={}, orgCode={}", userId, organizationCode);
                return;
            }

            // 예금계좌 동기화
            if (accountList.getData().getResDepositTrust() != null) {
                accountSyncService.syncDepositAccounts(userId, organizationCode,
                        accountList.getData().getResDepositTrust(), connectedId);
            }

            // 대출계좌 동기화
            if (accountList.getData().getResLoan() != null) {
                accountSyncService.syncLoanAccounts(userId, organizationCode,
                        accountList.getData().getResLoan(), connectedId);
            }

        } catch (Exception e) {
            log.error("기관별 계좌 데이터 동기화 실패: userId={}, orgCode={}", userId, organizationCode, e);
        }
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
}

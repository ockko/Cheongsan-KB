package cheongsan.domain.codef.service;

import cheongsan.domain.codef.dto.AccountListResponseDTO;

import java.util.List;

/**
 * CODEF 계좌 동기화 서비스 인터페이스
 */
public interface CodefAccountSyncService {
    /**
     * 예금계좌 동기화
     */
    void syncDepositAccounts(Long userId, String organizationCode,
                             List<AccountListResponseDTO.DepositAccount> depositAccounts,
                             String connectedId);

    /**
     * 대출계좌 동기화
     */
    void syncLoanAccounts(Long userId, String organizationCode,
                          List<AccountListResponseDTO.LoanAccount> loanAccounts,
                          String connectedId);
}

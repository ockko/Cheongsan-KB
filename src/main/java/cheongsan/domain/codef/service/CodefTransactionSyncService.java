package cheongsan.domain.codef.service;

/**
 * CODEF 거래내역 동기화 서비스 인터페이스
 */
public interface CodefTransactionSyncService {
    /**
     * 예금 거래내역 동기화
     */
    void syncDepositTransactionHistory(Long userId, String connectedId,
                                       String organizationCode, String accountNumber);

    /**
     * 대출 거래내역 동기화
     */
    void syncLoanTransactionHistory(Long userId, String connectedId,
                                    String organizationCode, String accountNumber);
}

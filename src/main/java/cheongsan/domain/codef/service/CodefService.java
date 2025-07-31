package cheongsan.domain.codef.service;

import cheongsan.domain.codef.dto.AccountListResponseDTO;
import cheongsan.domain.codef.dto.ConnectedIdRequestDTO;
import cheongsan.domain.codef.dto.LoanTransactionResponseDTO;
import cheongsan.domain.codef.dto.TransactionListResponseDTO;

public interface CodefService {
    /**
     * CODEF 토큰 발급
     */
    String getAccessToken();

    /**
     * Connected ID 생성
     */
    String createConnectedId(ConnectedIdRequestDTO requestDTO);

    /**
     * 보유 계좌 조회 (예금계좌 + 대출계좌)
     */
    AccountListResponseDTO getAccountList(String connectedId, String organizationCode);

    /**
     * 수시입출 거래내역 조회
     */
    TransactionListResponseDTO getTransactionListWithDates(String connectedId, String organizationCode, String accountNumber, String startDate, String endDate);

    /**
     * 대출 거래내역 조회
     */
    LoanTransactionResponseDTO getLoanTransactionList(String connectedId, String organizationCode, String accountNumber, String startDate, String endDate);
}

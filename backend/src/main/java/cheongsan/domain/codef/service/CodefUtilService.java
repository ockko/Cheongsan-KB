package cheongsan.domain.codef.service;

import cheongsan.domain.codef.dto.AccountListResponseDTO;
import cheongsan.domain.debt.entity.DebtAccount;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * CODEF 유틸리티 서비스 인터페이스
 */
public interface CodefUtilService {
    /**
     * 금융기관 코드 조회 또는 생성
     */
    Long getOrCreateFinancialInstitution(String organizationCode);

    /**
     * 기관 코드를 기관명으로 변환
     */
    String getOrganizationName(String organizationCode);

    /**
     * 대출계좌 엔티티 변환
     */
    DebtAccount convertToDebtAccount(Long userId, Long organizationCode,
                                     AccountListResponseDTO.LoanAccount account);

    /**
     * 날짜 문자열 파싱
     */
    LocalDate parseDate(String dateString);

    /**
     * 거래 날짜/시간 파싱
     */
    LocalDateTime parseTransactionDateTime(String dateString, String timeString);
}

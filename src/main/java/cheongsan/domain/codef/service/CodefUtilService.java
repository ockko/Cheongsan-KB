package cheongsan.domain.codef.service;

import cheongsan.domain.codef.dto.AccountListResponseDTO;
import cheongsan.domain.debt.entity.DebtAccount;
import cheongsan.domain.debt.mapper.FinancialInstitutionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 유틸리티
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class CodefUtilService {
    private final FinancialInstitutionMapper financialInstitutionMapper;

    /**
     * 금융기관 코드 조회 또는 생성
     */
    public Long getOrCreateFinancialInstitution(String organizationCode) {
        String organizationName = getOrganizationName(organizationCode);
        Long orgCodeLong = financialInstitutionMapper.findCodeByName(organizationName);

        if (orgCodeLong == null) {
            financialInstitutionMapper.insertInstitution(organizationName);
            orgCodeLong = financialInstitutionMapper.findCodeByName(organizationName);
        }
        return orgCodeLong;
    }

    /**
     * 기관 코드를 기관명으로 변환
     */
    public String getOrganizationName(String organizationCode) {
        Map<String, String> codeToNameMap = new HashMap<>();
        codeToNameMap.put("0081", "하나은행");
        codeToNameMap.put("0020", "우리은행");
        codeToNameMap.put("0088", "신한은행");
        codeToNameMap.put("0004", "KB국민은행");
        codeToNameMap.put("0011", "농협은행");

        return codeToNameMap.getOrDefault(organizationCode, "기타금융기관");
    }

    /**
     * 대출계좌 엔티티 변환
     */
    public DebtAccount convertToDebtAccount(Long userId, Long organizationCode,
                                            AccountListResponseDTO.LoanAccount account) {
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

    /**
     * 날짜 문자열 파싱
     */
    public LocalDate parseDate(String dateString) {
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

    /**
     * 거래 날짜/시간 파싱
     */
    public LocalDateTime parseTransactionDateTime(String dateString, String timeString) {
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

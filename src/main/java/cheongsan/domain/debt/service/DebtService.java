package cheongsan.domain.debt.service;

import cheongsan.domain.debt.dto.*;
import cheongsan.domain.user.dto.DebtUpdateRequestDTO;
import cheongsan.domain.user.dto.DebtUpdateResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface DebtService {
    List<InitialDebtDTO> getUserInitialLoanList(Long userId);

    void updateInitialLoanInfo(Long userId, RepaymentInfoRequestDTO dto);

    List<DebtInfoResponseDTO> getUserDebtList(Long userId);

    DebtDetailResponseDTO getLoanDetail(Long userId, Long loanId);

    void registerDebt(DebtRegisterRequestDTO dto, Long userId);

    BigDecimal calculateTotalMonthlyPayment(Long userId);

    // 월별 상환일자 조회
    List<RepaymentCalendarDTO> getMonthlyRepayments(Long userId, int year, int month);

    // 특정 날짜의 상환일자 상세 조회
    List<DailyRepaymentDTO> getDailyRepayments(Long userId, LocalDate date);

    RepaymentRatioResponseDTO getRepaymentRatio(Long userId);

    // 연체 일수 계산 및 연체 대출 조회
    List<DelinquentLoanResponseDTO> getDelinquentLoans(Long userId);

    DebtUpdateResponseDTO updateDebtAccount(Long debtAccountId, DebtUpdateRequestDTO dto);
}
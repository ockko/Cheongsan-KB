package cheongsan.domain.debt.service;

import cheongsan.domain.debt.dto.DebtDetailResponseDTO;
import cheongsan.domain.debt.dto.DebtInfoResponseDTO;
import cheongsan.domain.debt.dto.DebtRegisterRequestDTO;
import cheongsan.domain.debt.dto.DailyRepaymentDTO;
import cheongsan.domain.debt.dto.RepaymentCalendarDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface DebtService {
    List<DebtInfoResponseDTO> getUserDebtList(Long userId, String sort);

    DebtDetailResponseDTO getLoanDetail(Long loanId);

    void registerDebt(DebtRegisterRequestDTO dto, Long userId);

    BigDecimal calculateTotalMonthlyPayment(Long userId);

    // 월별 상환일자 조회
    List<RepaymentCalendarDTO> getMonthlyRepayments(Long userId, int year, int month);

    // 특정 날짜의 상환일자 상세 조회
    List<DailyRepaymentDTO> getDailyRepayments(Long userId, LocalDate date);
}
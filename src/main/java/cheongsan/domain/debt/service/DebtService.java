package cheongsan.domain.debt.service;

import cheongsan.domain.debt.dto.DailyRepaymentDTO;
import cheongsan.domain.debt.dto.DebtDetailDTO;
import cheongsan.domain.debt.dto.DebtInfoDTO;
import cheongsan.domain.debt.dto.RepaymentCalendarDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface DebtService {
    public List<DebtInfoDTO> getUserDebtList(Long userId, String sort);

    public DebtDetailDTO getLoanDetail(Long loanId);

    BigDecimal calculateTotalMonthlyPayment(Long userId);

    // 월별 상환일자 조회
    List<RepaymentCalendarDTO> getMonthlyRepayments(Long userId, int year, int month);

    // 특정 날짜의 상환일자 상세 조회
    List<DailyRepaymentDTO> getDailyRepayments(Long userId, LocalDate date);
}

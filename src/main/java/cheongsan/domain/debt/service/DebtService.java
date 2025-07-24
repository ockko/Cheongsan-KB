package cheongsan.domain.debt.service;

import cheongsan.domain.debt.dto.DebtDetailDTO;
import cheongsan.domain.debt.dto.DebtInfoDTO;

import java.math.BigDecimal;
import java.util.List;

public interface DebtService {
    public List<DebtInfoDTO> getUserDebtList(Long userId, String sort);

    public DebtDetailDTO getLoanDetail(Long loanId);

    BigDecimal calculateTotalMonthlyPayment(Long userId);
}

package cheongsan.domain.debt.service;

import cheongsan.domain.debt.dto.DebtInfoDTO;

import java.math.BigDecimal;
import java.util.List;

public interface DebtService {

    public List<DebtInfoDTO> getLoansByUserId(Long userId, String sort);

    BigDecimal calculateTotalMonthlyPayment(Long userId);
}

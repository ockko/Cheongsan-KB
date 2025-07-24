package cheongsan.domain.debt.service;

import cheongsan.domain.debt.dto.request.DebtRegisterDTO;
import cheongsan.domain.debt.dto.response.DebtDetailDTO;
import cheongsan.domain.debt.dto.response.DebtInfoDTO;

import java.util.List;

public interface DebtService {
    List<DebtInfoDTO> getUserDebtList(Long userId, String sort);

    DebtDetailDTO getLoanDetail(Long loanId);

    void registerDebt(DebtRegisterDTO dto, Long userId);
}
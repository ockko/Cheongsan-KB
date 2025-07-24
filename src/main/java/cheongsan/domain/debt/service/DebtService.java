package cheongsan.domain.debt.service;

import cheongsan.domain.debt.dto.DebtDetailResponseDTO;
import cheongsan.domain.debt.dto.DebtInfoResponseDTO;
import cheongsan.domain.debt.dto.DebtRegisterRequestDTO;

import java.util.List;

public interface DebtService {
    List<DebtInfoResponseDTO> getUserDebtList(Long userId, String sort);

    DebtDetailResponseDTO getLoanDetail(Long loanId);

    void registerDebt(DebtRegisterRequestDTO dto, Long userId);
}
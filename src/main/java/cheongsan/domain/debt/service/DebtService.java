package cheongsan.domain.debt.service;

import cheongsan.domain.debt.dto.DebtInfoDTO;

import java.util.List;

public interface DebtService {
    public List<DebtInfoDTO> getUserDebtList(Long userId, String sort);
}
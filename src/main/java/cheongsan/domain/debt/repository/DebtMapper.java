package cheongsan.domain.debt.repository;

import cheongsan.domain.debt.dto.DebtDTO;
import cheongsan.domain.debt.dto.DebtDetailDTO;
import cheongsan.domain.debt.dto.DebtInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DebtMapper {
    List<DebtInfoDTO> getUserDebtList(Long userId);

    DebtDetailDTO getLoanDetail(Long loanId);

    List<DebtDTO> findByUserId(Long userId);
}
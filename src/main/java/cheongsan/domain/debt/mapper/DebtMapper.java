package cheongsan.domain.debt.mapper;

import cheongsan.domain.debt.dto.response.DebtDetailDTO;
import cheongsan.domain.debt.dto.response.DebtInfoDTO;
import cheongsan.domain.debt.entity.DebtAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DebtMapper {
    List<DebtInfoDTO> getUserDebtList(Long userId);

    DebtDetailDTO getLoanDetail(Long loanId);

    void insertDebt(DebtAccount debtAccount);

    boolean isDebtAccountExists(@Param("userId") Long userId,
                                @Param("resAccount") String resAccount);

}
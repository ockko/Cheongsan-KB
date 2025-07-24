package cheongsan.domain.debt.mapper;

import cheongsan.domain.debt.dto.DebtDetailResponseDTO;
import cheongsan.domain.debt.dto.DebtInfoResponseDTO;
import cheongsan.domain.debt.entity.DebtAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DebtMapper {
    List<DebtInfoResponseDTO> getUserDebtList(Long userId);

    DebtDetailResponseDTO getLoanDetail(Long loanId);

    // 대출 상품 추가
    void insertDebt(DebtAccount debtAccount);

    // 대출 계좌 존재 여부 확인
    boolean isDebtAccountExists(@Param("userId") Long userId,
                                @Param("resAccount") String resAccount);

}
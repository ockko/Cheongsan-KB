package cheongsan.domain.debt.mapper;

import cheongsan.domain.debt.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DebtMapper {
    List<DebtInfoDTO> getUserDebtList(Long userId);

    DebtDetailDTO getLoanDetail(Long loanId);

    List<DebtDTO> findByUserId(Long userId);

    // 월별 상환일자 조회
    List<RepaymentCalendarDTO> getMonthlyRepayments(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );

    // 특정 날짜의 상환일자 상세 조회
    List<DailyRepaymentDTO> getDailyRepayments(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );
}
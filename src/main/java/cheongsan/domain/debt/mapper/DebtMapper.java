package cheongsan.domain.debt.mapper;

import cheongsan.domain.debt.dto.DailyRepaymentDTO;
import cheongsan.domain.debt.dto.RepaymentCalendarDTO;
import cheongsan.domain.debt.entity.DebtAccount;
import cheongsan.domain.debt.entity.DebtRepaymentRatio;
import cheongsan.domain.debt.entity.DelinquentLoan;
import cheongsan.domain.debt.entity.FinancialInstitution;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DebtMapper {
    List<DebtAccount> getUserDebtList(Long userId);

    // 대출 상세 조회 (1)
    DebtAccount getDebtAccountById(Long loanId);

    // 대출 상세 조회 (2)
    FinancialInstitution getFinancialInstitutionByCode(Long organizationCode);

    /**
     * 사용자 ID와 계좌번호로 대출계좌 조회
     */
    DebtAccount findByUserIdAndAccount(@Param("userId") Long userId,
                                       @Param("resAccount") String resAccount);

    List<DebtAccount> findByUserId(Long userId);

    // 대출 상품 추가
    void insertDebt(DebtAccount debtAccount);

    // 대출 계좌 존재 여부 확인
    boolean isDebtAccountExists(@Param("userId") Long userId,
                                @Param("resAccount") String resAccount);

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

    // 상환율 계산할 데이터 조회
    List<DebtRepaymentRatio> getDebtRepaymentInfoByUserId(@Param("userId") Long userId);

    // 연체된 대출 조회
    List<DelinquentLoan> getDelinquentLoanByUserId(Long userId);

    // 수정된 대출 계좌 update
    void updateDebt(@Param("gracePeriodMonths") Long gracePeriodMonths,
                    @Param("repaymentMethod") String repaymentMethod,
                    @Param("nextPaymentDate") LocalDate nextPaymentDate,
                    @Param("debtAccountId") Long debtAccountId);


}
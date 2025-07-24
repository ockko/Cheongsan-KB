package cheongsan.domain.debt.service;

import cheongsan.common.util.LoanCalculator;
import cheongsan.domain.debt.dto.*;
import cheongsan.domain.debt.mapper.DebtMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class DebtServiceImpl implements DebtService {
    private final DebtMapper debtMapper;
    private final LoanCalculator loanCalculator;

    @Override
    public List<DebtInfoDTO> getUserDebtList(Long userId, String sort) {
        List<DebtInfoDTO> debts = debtMapper.getUserDebtList(userId);

        // 상환율 계산
        for (DebtInfoDTO debt : debts) {
            if (debt.getOriginalAmount() != null && debt.getOriginalAmount() > 0) {
                double rate = 1 - ((double) debt.getCurrentBalance() / debt.getOriginalAmount());
                debt.setRepaymentRate(rate);
            } else {
                debt.setRepaymentRate(0.0);
            }
        }

        switch (sort) {
            case "interestRateDesc": // 이자율 높은 순
                debts.sort(Comparator.comparing(DebtInfoDTO::getInterestRate, Comparator.nullsLast(Double::compareTo)).reversed());
                break;

            case "repaymentRateDesc": // 상환율 높은 순
                debts.sort(Comparator.comparing(DebtInfoDTO::getRepaymentRate, Comparator.nullsLast(Double::compareTo)).reversed());
                break;

            case "startedAtAsc": // 오래된 순
                debts.sort(Comparator.comparing(DebtInfoDTO::getLoanStartDate, Comparator.nullsLast(java.util.Date::compareTo)));
                break;

            case "startedAtDesc": // 최신 순
                debts.sort(Comparator.comparing(DebtInfoDTO::getLoanStartDate, Comparator.nullsLast(java.util.Date::compareTo)).reversed());
                break;

//            case "recommended":
//                // 우리 앱 추천순 - 추후 구현
//                break;

            default:
        }

        return debts;
    }

    @Override
    public DebtDetailDTO getLoanDetail(Long loanId) {
        return debtMapper.getLoanDetail(loanId);
    }

    @Override
    public BigDecimal calculateTotalMonthlyPayment(Long userId) {
        List<DebtDTO> userDebts = debtMapper.findByUserId(userId);

        return userDebts.stream()
                .map(debt -> loanCalculator.calculateMonthlyPayment(
                        debt.getRepaymentMethodEnum(), // 상환방식
                        debt.getOriginalAmount(),      // 총 원금
                        debt.getCurrentBalance(),      // 현재 잔액
                        debt.getInterestRate(),        // 연이율
                        debt.getLoanStartDate(),       // 대출 시작일
                        debt.getLoanEndDate()          // 대출 만기일
                ))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<RepaymentCalendarDTO> getMonthlyRepayments(Long userId, int year, int month) {
        log.info("월별 상환일자 조회 - 사용자 ID: {}, 년도: {}, 월: {}", userId, year, month);

        List<RepaymentCalendarDTO> repayments = debtMapper.getMonthlyRepayments(userId, year, month);
        return repayments;
    }

    @Override
    public List<DailyRepaymentDTO> getDailyRepayments(Long userId, LocalDate date) {
        log.info("일별 상환일자 조회 - 사용자 ID: {}, 날짜: {}", userId, date);

        List<DailyRepaymentDTO> repayments = debtMapper.getDailyRepayments(userId, date);
        return repayments;
    }
}

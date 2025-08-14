package cheongsan.domain.debt.service;

import cheongsan.domain.debt.entity.DebtAccount;
import cheongsan.domain.debt.entity.DebtTransaction;
import cheongsan.domain.debt.mapper.DebtMapper;
import cheongsan.domain.debt.mapper.DebtTransactionMapper;
import cheongsan.domain.simulator.dto.MonthlyPaymentDetailDTO;
import cheongsan.domain.simulator.dto.RepaymentResponseDTO;
import cheongsan.domain.simulator.dto.StrategyType;
import cheongsan.domain.simulator.service.RepaymentSimulationService;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class RepaymentUpdateServiceImpl implements RepaymentUpdateService {

    private final DebtMapper debtMapper;
    private final DebtTransactionMapper debtTransactionMapper;
    private final RepaymentSimulationService simulationService;
    private final UserMapper userMapper;

    @Override
    public void updateNextPaymentDates(Long userId) {
        // 1. 유저 전략 가져오기
        String strategyStr = userMapper.findStrategyByUserId(userId);
        if (strategyStr == null) return;
        StrategyType strategyType = StrategyType.valueOf(strategyStr);

        // 2. 시뮬레이션 결과 가져오기
        RepaymentResponseDTO result = simulationService.getStrategy(userId, strategyType);
        if (result == null || result.getRepaymentHistory() == null) return;

        // 3. 모든 대출 조회
        List<DebtAccount> debts = debtMapper.getUserDebtList(userId);
        LocalDate today = LocalDate.now();

        for (DebtAccount debt : debts) {

            LocalDate nextPayment = debt.getNextPaymentDate();
            log.info("debt " + debt.getDebtName() + " nextPayment " + nextPayment);
            if (nextPayment == null || !nextPayment.isBefore(today)) continue;

            // 이번 달 상환 내역 확인
            LocalDate start = nextPayment;           // 상환일
            LocalDate end = today;                   // 오늘
            Long loanId = debt.getId();

            List<DebtTransaction> transactions = debtTransactionMapper.findTransactionsByLoanAndPeriod(
                    loanId, start, end
            );

            // 시뮬레이션 기준 월 상환 금액
            List<MonthlyPaymentDetailDTO> plans = result.getRepaymentHistory().get(debt.getDebtName());
            MonthlyPaymentDetailDTO monthlyPlan = null;

            if (plans != null && !plans.isEmpty()) {
                // 1. nextPayment 연 + 월과 일치하는 계획 찾기
                monthlyPlan = plans.stream()
                        .filter(p -> p.getPaymentDate().getYear() == nextPayment.getYear()
                                && p.getPaymentDate().getMonth() == nextPayment.getMonth())
                        .findFirst()
                        .orElse(plans.get(0)); // 2. 없으면 첫 번째 항목 사용
            }

            if (monthlyPlan == null) {
                log.warn("[RepaymentCheck] ❌ 시뮬레이션 상환 계획 없음 → next_payment_date 갱신 안 함, debtName={}", debt.getDebtName());
                continue;
            }


            // 합계 계산
            BigDecimal totalPrincipal = transactions.stream()
                    .map(DebtTransaction::getPrincipalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalInterest = transactions.stream()
                    .map(DebtTransaction::getInterestAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalPaid = totalPrincipal.add(totalInterest);
            BigDecimal requiredTotal = monthlyPlan.getPrincipal().add(monthlyPlan.getInterest());

            // 로그 추가
            log.info("[RepaymentCheck] loanId={}, debtName={}, 월 상환 계획 확인 → principal={}, interest={}, requiredTotal={}",
                    loanId,
                    debt.getDebtName(),
                    monthlyPlan.getPrincipal(),
                    monthlyPlan.getInterest(),
                    requiredTotal
            );

            log.info("[RepaymentCheck] loanId={}, debtName={}, 기간 {} ~ {}, 거래건수={}, totalPaid={}, requiredTotal={}",
                    loanId,
                    debt.getDebtName(),
                    start,
                    end,
                    transactions.size(),
                    totalPaid,
                    requiredTotal
            );
            if (totalPaid.compareTo(requiredTotal) >= 0) {
                LocalDate newNextPayment = nextPayment.plusMonths(1);
                debtMapper.updateNextPaymentDate(loanId, newNextPayment);
                log.info("[RepaymentCheck] next_payment_date 갱신 완료 → loanId={}, newDate={}", loanId, newNextPayment);
            } else {
                if (transactions.isEmpty()) {
                    log.warn("[RepaymentCheck] ❌ 상환 내역 없음 → next_payment_date 갱신 안 함");
                } else {
                    log.warn("[RepaymentCheck] ❌ 상환 미달 ({} / {}) → next_payment_date 갱신 안 함",
                            totalPaid, requiredTotal);
                }
            }

        }
    }
}

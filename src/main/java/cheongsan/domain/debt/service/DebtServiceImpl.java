package cheongsan.domain.debt.service;

import cheongsan.domain.debt.dto.*;
import cheongsan.common.util.LoanCalculator;
import cheongsan.domain.debt.entity.DebtAccount;
import cheongsan.domain.debt.mapper.DebtMapper;
import cheongsan.domain.debt.mapper.FinancialInstitutionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class DebtServiceImpl implements DebtService {
    private final DebtMapper debtMapper;
    private final LoanCalculator loanCalculator;
    private final FinancialInstitutionMapper financialInstitutionMapper;

    @Override
    public List<DebtInfoResponseDTO> getUserDebtList(Long userId, String sort) {
        List<DebtInfoResponseDTO> debts = debtMapper.getUserDebtList(userId);

        // 상환율 계산
        for (DebtInfoResponseDTO debt : debts) {
            if (debt.getOriginalAmount() != null && debt.getOriginalAmount() > 0) {
                double rate = 1 - ((double) debt.getCurrentBalance() / debt.getOriginalAmount());
                debt.setRepaymentRate(rate);
            } else {
                debt.setRepaymentRate(0.0);
            }
        }

        switch (sort) {
            case "interestRateDesc": // 이자율 높은 순
                debts.sort(Comparator.comparing(DebtInfoResponseDTO::getInterestRate, Comparator.nullsLast(Double::compareTo)).reversed());
                break;

            case "repaymentRateDesc": // 상환율 높은 순
                debts.sort(Comparator.comparing(DebtInfoResponseDTO::getRepaymentRate, Comparator.nullsLast(Double::compareTo)).reversed());
                break;

            case "startedAtAsc": // 오래된 순
                debts.sort(Comparator.comparing(DebtInfoResponseDTO::getLoanStartDate, Comparator.nullsLast(java.util.Date::compareTo)));
                break;

            case "startedAtDesc": // 최신 순
                debts.sort(Comparator.comparing(DebtInfoResponseDTO::getLoanStartDate, Comparator.nullsLast(java.util.Date::compareTo)).reversed());
                break;

//            case "recommended":
//                // 우리 앱 추천순 - 추후 구현
//                break;

            default:
        }

        return debts;
    }

    @Override
    public DebtDetailResponseDTO getLoanDetail(Long loanId) {
        return debtMapper.getLoanDetail(loanId);
    }

    @Override
    public void registerDebt(DebtRegisterRequestDTO dto, Long userId) {
        // 1. 금융기관 코드 조회 또는 등록
        Long organizationCode = financialInstitutionMapper.findCodeByName(dto.getOrganizationName());
        if (organizationCode == null) {
            financialInstitutionMapper.insertInstitution(dto.getOrganizationName());
            organizationCode = financialInstitutionMapper.findCodeByName(dto.getOrganizationName());
        }

        boolean exists = debtMapper.isDebtAccountExists(userId, dto.getResAccount());
        if (exists) {
            throw new RuntimeException("이미 등록된 대출 계좌입니다.");
        }

        // 2. 만기일 계산
        LocalDate loanEndDate = dto.getLoanStartDate().plusMonths(dto.getTotalMonths());

        // 3. 다음 상환일 계산 (예: 매달 1일 상환 기준이라면)
        LocalDate nextPaymentDate = dto.getLoanStartDate().plusMonths(1); // 예시

        // 4. DebtAccount 생성
        DebtAccount debt = DebtAccount.builder()
                .userId(userId)
                .organizationCode(organizationCode)
                .resAccount(dto.getResAccount())
                .debtName(dto.getDebtName())
                .currentBalance(dto.getCurrentBalance())
                .originalAmount(dto.getOriginalAmount())
                .interestRate(dto.getInterestRate())
                .loanStartDate(dto.getLoanStartDate())
                .loanEndDate(loanEndDate)
                .nextPaymentDate(nextPaymentDate)
                .gracePeriodMonths(dto.getGracePeriodMonths())
                .repaymentMethod(dto.getRepaymentMethod())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 5. DB 저장
        debtMapper.insertDebt(debt);
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

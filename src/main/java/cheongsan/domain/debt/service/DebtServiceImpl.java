package cheongsan.domain.debt.service;

import cheongsan.common.util.LoanCalculator;
import cheongsan.domain.debt.dto.*;
import cheongsan.domain.debt.entity.DebtAccount;
import cheongsan.domain.debt.entity.DebtRepaymentRatio;
import cheongsan.domain.debt.entity.DelinquentLoan;
import cheongsan.domain.debt.entity.FinancialInstitution;
import cheongsan.domain.debt.mapper.DebtMapper;
import cheongsan.domain.debt.mapper.FinancialInstitutionMapper;
import cheongsan.domain.simulator.dto.RepaymentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class DebtServiceImpl implements DebtService {
    private final DebtMapper debtMapper;
    private final LoanCalculator loanCalculator;
    private final FinancialInstitutionMapper financialInstitutionMapper;

    @Override
    public List<DebtInfoResponseDTO> getUserDebtList(Long userId) {
        List<DebtAccount> debts = debtMapper.getUserDebtList(userId);

        return debts.stream()
                .map(debt -> {
                    BigDecimal repaymentRate = BigDecimal.ZERO;
                    if (debt.getOriginalAmount() != null
                            && debt.getOriginalAmount().compareTo(BigDecimal.ZERO) > 0
                            && debt.getCurrentBalance() != null) {
                        repaymentRate = BigDecimal.ONE.subtract(
                                debt.getCurrentBalance().divide(debt.getOriginalAmount(), 6, RoundingMode.HALF_UP)
                        );
                    }

                    String organizationName = financialInstitutionMapper.findNameByCode(debt.getOrganizationCode());
                    return DebtInfoResponseDTO.builder()
                            .debtId(debt.getUserId()) // 필요 시 수정
                            .debtName(debt.getDebtName())
                            .organizationName(organizationName)
                            .originalAmount(debt.getOriginalAmount())
                            .currentBalance(debt.getCurrentBalance())
                            .interestRate(debt.getInterestRate())
                            .repaymentType(RepaymentType.fromRepaymentMethod(debt.getRepaymentMethod()))
                            .loanStartDate(debt.getLoanStartDate())
                            .loanEndDate(debt.getLoanEndDate())
                            .repaymentRate(repaymentRate)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<DebtInfoResponseDTO> sortDebtInfoList(List<DebtInfoResponseDTO> list, String sort) {
        if (list == null || list.isEmpty() || sort == null) {
            return list;
        }

        List<DebtInfoResponseDTO> sortedList = new ArrayList<>(list); // 원본 불변

        switch (sort) {
            case "interestRateDesc":
                sortedList.sort(Comparator.comparing(
                        DebtInfoResponseDTO::getInterestRate, Comparator.nullsLast(BigDecimal::compareTo)
                ).reversed());
                break;

            case "repaymentRateDesc":
                sortedList.sort(Comparator.comparing(
                        DebtInfoResponseDTO::getRepaymentRate, Comparator.nullsLast(BigDecimal::compareTo)
                ).reversed());
                break;

            case "startedAtAsc":
                sortedList.sort(Comparator.comparing(
                        DebtInfoResponseDTO::getLoanStartDate, Comparator.nullsLast(LocalDate::compareTo)
                ));
                break;

            case "startedAtDesc":
                sortedList.sort(Comparator.comparing(
                        DebtInfoResponseDTO::getLoanStartDate, Comparator.nullsLast(LocalDate::compareTo)
                ).reversed());
                break;

            default:
                break;
        }

        return sortedList;
    }

    @Override
    public DebtDetailResponseDTO getLoanDetail(Long loanId) {
        DebtAccount debtAccount = debtMapper.getDebtAccountById(loanId);

        FinancialInstitution fi = debtMapper.getFinancialInstitutionByCode(debtAccount.getOrganizationCode());

        // DTO 변환 및 반환
        return DebtDetailResponseDTO.fromEntity(debtAccount, fi);
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
        List<DebtAccount> userDebtsAsEntity = debtMapper.findByUserId(userId);

        List<DebtDTO> userDebtsAsDto = userDebtsAsEntity.stream()
                .map(DebtDTO::fromEntity)
                .collect(Collectors.toList());

        return userDebtsAsDto.stream()
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

    @Override
    public RepaymentRatioResponseDTO getRepaymentRatio(Long userId) {
        // 대출 상환율 계산에 필요한 데이터(DebtRepaymentRatio) 조회
        List<DebtRepaymentRatio> debts = debtMapper.getDebtRepaymentInfoByUserId(userId);

        // 변수 초기화
        BigDecimal totalOriginal = BigDecimal.ZERO; // 총 원금
        BigDecimal totalRepaid = BigDecimal.ZERO; // 총 상환금액

        for (DebtRepaymentRatio debt : debts) {
            if (debt.getOriginalAmount() != null && debt.getCurrentBalance() != null) {
                totalOriginal = totalOriginal.add(debt.getOriginalAmount()); // 총 원금 계산
                totalRepaid = totalRepaid.add(debt.getOriginalAmount().subtract(debt.getCurrentBalance())); // 총 상환액 계산 (상환액 : 원금 - 잔액)
            }
        }

        // 총 상환율 계산
        BigDecimal ratio = BigDecimal.ZERO;
        if (totalOriginal.compareTo(BigDecimal.ZERO) > 0) {
            ratio = totalRepaid.divide(totalOriginal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }

        return RepaymentRatioResponseDTO.builder()
                .totalOriginalAmount(totalOriginal)
                .totalRepaidAmount(totalRepaid)
                .repaymentRatio(ratio)
                .build();
    }

    @Override
    public List<DelinquentLoanResponseDTO> getDelinquentLoans(Long userId) {
        List<DelinquentLoan> entities = debtMapper.getDelinquentLoanByUserId(userId);

        return entities.stream()
                .map(entity -> {
                    int overdueDays = Period.between(entity.getNextPaymentDate(), LocalDate.now()).getDays();
                    return DelinquentLoanResponseDTO.builder()
                            .debtName(entity.getDebtName())
                            .organizationName(entity.getOrganizationName())
                            .overdueDays(overdueDays)
                            .build();
                })
                .sorted(Comparator.comparingInt(DelinquentLoanResponseDTO::getOverdueDays).reversed())
                .collect(Collectors.toList());
    }
}

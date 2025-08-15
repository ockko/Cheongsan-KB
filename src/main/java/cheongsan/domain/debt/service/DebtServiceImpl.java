package cheongsan.domain.debt.service;

import cheongsan.common.util.LoanCalculator;
import cheongsan.domain.debt.dto.*;
import cheongsan.domain.debt.entity.DebtAccount;
import cheongsan.domain.debt.entity.DebtRepaymentRatio;
import cheongsan.domain.debt.entity.DelinquentLoan;
import cheongsan.domain.debt.entity.FinancialInstitution;
import cheongsan.domain.debt.mapper.DebtMapper;
import cheongsan.domain.debt.mapper.FinancialInstitutionMapper;
import cheongsan.domain.simulator.dto.LoanDTO;
import cheongsan.domain.simulator.dto.RepaymentType;
import cheongsan.domain.user.dto.DebtUpdateRequestDTO;
import cheongsan.domain.user.dto.DebtUpdateResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
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
    public List<InitialDebtDTO> getUserInitialLoanList(Long userId) {
        List<DebtAccount> debts = debtMapper.getUserDebtList(userId);

        return debts.stream()
                .map(debt -> {
                    String organizationName = financialInstitutionMapper.findNameByCode(debt.getOrganizationCode());
                    return InitialDebtDTO.builder()
                            .debtId(debt.getId())
                            .organizationName(organizationName)
                            .debtName(debt.getDebtName())
                            .build();
                })
                .collect(Collectors.toList());
    }

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
                            .debtId(debt.getId())
                            .debtName(debt.getDebtName())
                            .organizationName(organizationName)
                            .originalAmount(debt.getOriginalAmount())
                            .currentBalance(debt.getCurrentBalance())
                            .interestRate(debt.getInterestRate())
                            .repaymentType(RepaymentType.fromRepaymentMethod(debt.getRepaymentMethod()))
                            .loanStartDate(debt.getLoanStartDate())
                            .loanEndDate(debt.getLoanEndDate())
                            .nextPaymentDate(debt.getNextPaymentDate())
                            .repaymentRate(repaymentRate)
                            .gracePeriodMonths(debt.getGracePeriodMonths())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<DebtInfoResponseDTO> sortDebtInfoList(List<DebtInfoResponseDTO> list, String sort) {
        if (list == null || list.isEmpty() || sort == null) {
            return list;
        }
        List<DebtInfoResponseDTO> sortedList = new ArrayList<>(list);

        switch (sort) {
            case "startedAtAsc": // 오래된 순
                sortedList.sort(Comparator.comparing(
                        DebtInfoResponseDTO::getLoanStartDate, Comparator.nullsLast(LocalDate::compareTo)
                ));
                break;

            case "createdAtDesc": // 최신 순 == startedAt 내림차순
            default:
                sortedList.sort(Comparator.comparing(
                        DebtInfoResponseDTO::getLoanStartDate, Comparator.nullsLast(LocalDate::compareTo)
                ).reversed());
                break;

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

            case "recommended":
                sortedList.sort(
                        Comparator.comparing((DebtInfoResponseDTO d) -> !isPrivateDebt(d))                       // 사금융 false이 먼저
                                .thenComparing(DebtInfoResponseDTO::getInterestRate, Comparator.reverseOrder())       // 이자율 내림차순
                );
                break;
        }
        return sortedList;
    }

    private boolean isPrivateDebt(DebtInfoResponseDTO debt) {
        String debtOrganizationName = debt.getOrganizationName();
        String type=debtMapper.findInstitutionTypeByOrganizationName(debtOrganizationName);
        return List.of("사금융", "카드론", "대부업", "P2P","기타").stream()
                .anyMatch(type::contains);  // 부분 문자열 검사
    }




    @Override
    public DebtDetailResponseDTO getLoanDetail(Long userId, Long loanId) {
        DebtAccount debtInfo = debtMapper.findDebtAccountById(loanId, userId);
        if (debtInfo == null) {
            log.warn("대출 정보 없음 - userId: {}, loanId: {}", userId, loanId);
            throw new IllegalArgumentException("해당 대출 정보가 존재하지 않거나 권한이 없습니다.");
        }
        log.debug("대출 정보 조회 성공 - debtInfo: {}", debtInfo);

        FinancialInstitution fi = debtMapper.getFinancialInstitutionByCode(debtInfo.getOrganizationCode());
        if (fi == null) {
            log.warn("금융기관 정보 없음 - organizationCode: {}", debtInfo.getOrganizationCode());
        } else {
            log.debug("금융기관 정보 조회 성공 - fi: {}", fi);
        }

        // DTO 변환 및 반환
        return DebtDetailResponseDTO.fromEntity(debtInfo, fi);
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


// 오늘 날짜 가져오기 (예시: LocalDate.now() 또는 전달된 날짜)
        LocalDate today = LocalDate.now();
        int nextPaymentDay = Math.toIntExact(dto.getNextPaymentDay());

// 이번 달의 상환일을 구함
        LocalDate thisMonthPaymentDate;
        try {
            thisMonthPaymentDate = today.withDayOfMonth(nextPaymentDay);
        } catch (DateTimeException e) {
            // 이번 달에 해당 일이 없으면 마지막 날로
            thisMonthPaymentDate = today.withDayOfMonth(today.lengthOfMonth());
        }

// 만약 오늘이 상환일 이후(혹은 당일 포함)이면, 반드시 다음 달로 넘어가야 함
        LocalDate baseDate;
        if (!today.isBefore(thisMonthPaymentDate)) {
            // 다음 달
            baseDate = today.plusMonths(1).withDayOfMonth(1);
        } else {
            // 아직 다다르지 않은 경우 이번 달
            baseDate = today.withDayOfMonth(1);
        }

// 다음 달 정보 계산
        LocalDate nextMonth = baseDate;
// 다음 달에 nextPaymentDay 존재 여부 확인
        LocalDate nextPaymentDate;
        try {
            nextPaymentDate = nextMonth.withDayOfMonth(nextPaymentDay);
        } catch (DateTimeException e) {
            // 없는 경우(예: 2월 30일), 마지막 날로 처리
            nextPaymentDate = nextMonth.withDayOfMonth(nextMonth.lengthOfMonth());
        }


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

    @Override
    public DebtUpdateResponseDTO updateDebtAccount(Long debtId, DebtUpdateRequestDTO dto) {
        DebtAccount account = debtMapper.getDebtAccountById(debtId);
        log.info(account);

        if (account == null) {
            throw new IllegalArgumentException("존재하지 않는 부채 계좌입니다.");
        }

        debtMapper.updateDebt(dto.getGracePeriodMonths(), dto.getRepaymentMethod(), dto.getNextPaymentDate(), debtId);

        return DebtUpdateResponseDTO.builder()
                .debtId(debtId)
                .organizationCode(account.getOrganizationCode())
                .debtName(account.getDebtName())
                .currentBalance(account.getCurrentBalance())
                .gracePeriodMonths(dto.getGracePeriodMonths())
                .repaymentMethod(dto.getRepaymentMethod())
                .build();
    }
}

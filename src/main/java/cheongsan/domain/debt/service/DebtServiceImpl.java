package cheongsan.domain.debt.service;

import cheongsan.domain.debt.dto.request.DebtRegisterDTO;
import cheongsan.domain.debt.dto.response.DebtDetailDTO;
import cheongsan.domain.debt.dto.response.DebtInfoDTO;
import cheongsan.domain.debt.entity.DebtAccount;
import cheongsan.domain.debt.mapper.DebtMapper;
import cheongsan.domain.debt.mapper.FinancialInstitutionMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class DebtServiceImpl implements DebtService {
    private final DebtMapper debtMapper;
    private final FinancialInstitutionMapper financialInstitutionMapper;

    public DebtServiceImpl(DebtMapper debtMapper, FinancialInstitutionMapper financialInstitutionMapper) {
        this.debtMapper = debtMapper;
        this.financialInstitutionMapper = financialInstitutionMapper;
    }

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
    public void registerDebt(DebtRegisterDTO dto, Long userId) {
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
}
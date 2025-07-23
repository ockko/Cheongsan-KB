package cheongsan.domain.debt.service;

import cheongsan.domain.debt.dto.DebtDetailDTO;
import cheongsan.domain.debt.dto.DebtInfoDTO;
import cheongsan.domain.debt.repository.DebtRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class DebtServiceImpl implements DebtService {
    private final DebtRepository debtRepository;

    public DebtServiceImpl(DebtRepository debtRepository) {
        this.debtRepository = debtRepository;
    }

    @Override
    public List<DebtInfoDTO> getUserDebtList(Long userId, String sort) {
        List<DebtInfoDTO> debts = debtRepository.getUserDebtList(userId);

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
        return debtRepository.getLoanDetail(loanId);
    }
}
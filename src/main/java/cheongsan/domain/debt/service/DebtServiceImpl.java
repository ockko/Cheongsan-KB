package cheongsan.domain.debt.service;

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
    public List<DebtInfoDTO> getDebtByUserId(Long userId, String sort) {
        List<DebtInfoDTO> debts = debtRepository.getLoansByUserId(userId);

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
            case "amountAsc":
                debts.sort(Comparator.comparing(DebtInfoDTO::getOriginalAmount));
                break;
            case "amountDesc":
                debts.sort(Comparator.comparing(DebtInfoDTO::getOriginalAmount).reversed());
                break;
            case "balanceAsc":
                debts.sort(Comparator.comparing(DebtInfoDTO::getCurrentBalance));
                break;
            case "balanceDesc":
                debts.sort(Comparator.comparing(DebtInfoDTO::getCurrentBalance).reversed());
                break;
            case "repaymentAsc":
                debts.sort(Comparator.comparing(DebtInfoDTO::getRepaymentRate));
                break;
            case "repaymentDesc":
                debts.sort(Comparator.comparing(DebtInfoDTO::getRepaymentRate).reversed());
                break;
            // 필요시 더 추가 가능
            default:
                // 아무 정렬도 하지 않음
        }

        return debts;
    }
}

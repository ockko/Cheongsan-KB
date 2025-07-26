package cheongsan.domain.simulator.service;

import cheongsan.common.util.DsrCalculator;
import cheongsan.domain.simulator.dto.LoanProductDTO;
import cheongsan.domain.simulator.dto.LoanRecommendationRequestDTO;
import cheongsan.domain.simulator.repository.LoanProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanRecommendationService {

    private final LoanProductRepository loanProductRepository;

    private static final BigDecimal DSR_LIMIT = new BigDecimal("0.4"); // 40%

    public List<LoanProductDTO> recommendLoans(LoanRecommendationRequestDTO request) {
        validateRequest(request);

        BigDecimal dsr = DsrCalculator.calculateDsr(
                request.getMonthlyRepayment(),
                request.getAnnualIncome()
        );

        return loanProductRepository.findRecommendedLoans(
                request.getPrincipal(),
                dsr,
                DSR_LIMIT
        );
    }

    private void validateRequest(LoanRecommendationRequestDTO request) {
        if (request.getPrincipal() == null || request.getPrincipal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("대출 원금은 0보다 커야 합니다.");
        }
        if (request.getTerm() <= 0 || request.getTerm() > 600) {
            throw new IllegalArgumentException("대출 기간은 1개월 이상 600개월 이하여야 합니다.");
        }
        if (request.getMonthlyRepayment() == null || request.getMonthlyRepayment().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("월 상환액은 0보다 커야 합니다.");
        }
        if (request.getAnnualIncome() == null || request.getAnnualIncome().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("연소득은 0보다 커야 합니다.");
        }

        BigDecimal dsr = DsrCalculator.calculateDsr(
                request.getMonthlyRepayment(),
                request.getAnnualIncome()
        );

        if (dsr.compareTo(DSR_LIMIT) > 0) {
            throw new IllegalArgumentException(String.format(
                    "DSR %.1f%%가 허용 한도 %.1f%%를 초과합니다.",
                    dsr.multiply(BigDecimal.valueOf(100)),
                    DSR_LIMIT.multiply(BigDecimal.valueOf(100))
            ));
        }
    }
}


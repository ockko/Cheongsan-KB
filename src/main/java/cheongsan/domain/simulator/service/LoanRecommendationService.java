package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.LoanRecommendationRequestDTO;
import cheongsan.domain.simulator.dto.RecommendationResultDTO;

import java.math.BigDecimal;

public interface LoanRecommendationService {
    RecommendationResultDTO recommendLoans(LoanRecommendationRequestDTO request, BigDecimal monthlyRepayment, BigDecimal dsr);
}

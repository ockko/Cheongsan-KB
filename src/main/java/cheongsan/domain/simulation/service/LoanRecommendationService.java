package cheongsan.domain.simulation.service;

import cheongsan.domain.simulation.dto.LoanRecommendationRequestDTO;
import cheongsan.domain.simulation.dto.RecommendationResultDTO;

import java.math.BigDecimal;

public interface LoanRecommendationService {
    RecommendationResultDTO recommendLoans(LoanRecommendationRequestDTO request, BigDecimal monthlyRepayment, BigDecimal dsr);
}

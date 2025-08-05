package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.LoanProductDTO;
import cheongsan.domain.simulator.dto.LoanRecommendationRequestDTO;

import java.math.BigDecimal;
import java.util.List;

public interface LoanRecommendationService {
    List<LoanProductDTO> recommendLoans(LoanRecommendationRequestDTO request, BigDecimal monthlyRepayment);
}

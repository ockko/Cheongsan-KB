package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.LoanProductDTO;
import cheongsan.domain.simulator.dto.LoanRecommendationRequestDTO;

import java.util.List;

public interface LoanRecommendationService {
    List<LoanProductDTO> recommendLoans(LoanRecommendationRequestDTO request);
}

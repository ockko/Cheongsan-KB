package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.*;

import java.math.BigDecimal;
import java.util.List;

public interface LoanSimulationService {
    LoanAnalyzeResponseDTO analyze(LoanAnalyzeRequestDTO request, Long userId);

    BigDecimal computeDsr(Long userId, LoanRecommendationRequestDTO request);

    TotalComparisonResultDTO compareTotalRepaymentWithNewLoan(List<LoanDTO> existingLoans, LoanDTO newLoan);

    InterestComparisonResultDTO compareInterestWithNewLoan(List<LoanDTO> existingLoans, LoanDTO newLoan);

}

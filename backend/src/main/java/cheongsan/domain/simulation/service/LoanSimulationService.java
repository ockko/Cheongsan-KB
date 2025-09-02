package cheongsan.domain.simulation.service;

import cheongsan.domain.policy.dto.MonthlyInterestComparisonDTO;
import cheongsan.domain.simulation.dto.*;

import java.math.BigDecimal;
import java.util.List;

public interface LoanSimulationService {
    LoanAnalyzeResponseDTO analyze(LoanAnalyzeRequestDTO request, Long userId);

    BigDecimal computeDsr(Long userId, LoanRecommendationRequestDTO request);

    TotalComparisonResultDTO compareTotalRepaymentWithNewLoan(List<LoanDTO> existingLoans, LoanDTO newLoan);

    InterestComparisonResultDTO compareInterestWithNewLoan(List<LoanDTO> existingLoans, LoanDTO newLoan);

    List<MonthlyInterestComparisonDTO> getMonthlyInterestComparison(Long userId,
                                                                    LoanAnalyzeRequestDTO newLoan,
                                                                    Scenario scenario,
                                                                    BigDecimal monthlyPrepayment);
}

package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.*;

import java.util.List;

public interface LoanSimulationService {
    LoanAnalyzeResponseDTO analyze(LoanAnalyzeRequestDTO request);

    TotalComparisonResultDTO compareTotalRepaymentWithNewLoan(List<LoanDTO> existingLoans, LoanDTO newLoan);

    InterestComparisonResultDTO compareInterestWithNewLoan(List<LoanDTO> existingLoans, LoanDTO newLoan);
}

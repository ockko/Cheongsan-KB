package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.LoanAnalyzeRequestDTO;
import cheongsan.domain.simulator.dto.LoanAnalyzeResponseDTO;
import cheongsan.domain.simulator.dto.LoanDTO;
import cheongsan.domain.simulator.dto.TotalComparisonResultDTO;

import java.util.List;

public interface LoanSimulationService {
    LoanAnalyzeResponseDTO analyze(LoanAnalyzeRequestDTO request);

    TotalComparisonResultDTO compareTotalRepaymentWithNewLoan(List<LoanDTO> existingLoans, LoanDTO newLoan);
}

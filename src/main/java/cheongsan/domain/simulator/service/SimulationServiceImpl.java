package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.LoanDTO;
import cheongsan.domain.simulator.dto.RepaymentRequestDTO;
import cheongsan.domain.simulator.dto.RepaymentResponseDTO;
import cheongsan.domain.simulator.service.strategy.RepaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimulationServiceImpl implements SimulationService {

    private final List<RepaymentStrategy> strategies;

    @Override
    public List<RepaymentResponseDTO> simulateAll(RepaymentRequestDTO request) {
        List<LoanDTO> loans = request.getLoans();

        return strategies.stream()
                .map(strategy -> {
                    List<LoanDTO> loanCopies = loans.stream()
                            .map(loan -> LoanDTO.builder()
                                    .id(loan.getId())
                                    .loanName(loan.getLoanName())
                                    .principal(loan.getPrincipal())
                                    .interestRate(loan.getInterestRate())
                                    .startDate(loan.getStartDate())
                                    .endDate(loan.getEndDate())
                                    .institutionType(loan.getInstitutionType())
                                    .repaymentType(loan.getRepaymentType())
                                    .prepaymentFeeRate(loan.getPrepaymentFeeRate())
                                    .paymentDate(loan.getPaymentDate())
                                    .build())
                            .collect(Collectors.toList());
                    return strategy.simulate(request, loanCopies);
                })
                .collect(Collectors.toList());

    }
}

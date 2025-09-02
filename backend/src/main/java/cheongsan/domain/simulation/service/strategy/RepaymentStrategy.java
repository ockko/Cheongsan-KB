package cheongsan.domain.simulation.service.strategy;

import cheongsan.domain.simulation.dto.LoanDTO;
import cheongsan.domain.simulation.dto.RepaymentRequestDTO;
import cheongsan.domain.simulation.dto.RepaymentResponseDTO;
import cheongsan.domain.simulation.dto.StrategyType;

import java.util.List;

public interface RepaymentStrategy {

    RepaymentResponseDTO simulate(RepaymentRequestDTO request, List<LoanDTO> loans);

    StrategyType getStrategyType(); // 전략 식별용
}

package cheongsan.domain.simulator.service.strategy;

import cheongsan.domain.simulator.dto.LoanDTO;
import cheongsan.domain.simulator.dto.RepaymentRequestDTO;
import cheongsan.domain.simulator.dto.RepaymentResponseDTO;
import cheongsan.domain.simulator.dto.StrategyType;

import java.util.List;

public interface RepaymentStrategy {

    RepaymentResponseDTO simulate(RepaymentRequestDTO request, List<LoanDTO> loans);

    StrategyType getStrategyType(); // 전략 식별용
}

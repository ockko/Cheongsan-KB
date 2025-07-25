package cheongsan.domain.simulator.service.strategy;

import cheongsan.domain.simulator.dto.RepaymentRequestDTO;
import cheongsan.domain.simulator.dto.RepaymentResultDTO;
import cheongsan.domain.simulator.dto.StrategyType;

public interface RepaymentStrategy {

    RepaymentResultDTO simulate(RepaymentRequestDTO request);

    StrategyType getStrategyType(); // 전략 식별용
}

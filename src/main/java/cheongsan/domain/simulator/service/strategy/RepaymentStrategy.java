package cheongsan.domain.simulator.service.strategy;

import cheongsan.domain.simulator.dto.*;

public interface RepaymentStrategy {

    RepaymentResultDto simulate(RepaymentRequestDto request);
    StrategyType getStrategyType(); // 전략 식별용
}

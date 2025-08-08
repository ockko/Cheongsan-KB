package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.RepaymentRequestDTO;
import cheongsan.domain.simulator.dto.RepaymentResponseDTO;
import cheongsan.domain.simulator.dto.StrategyType;

import java.util.List;

public interface RepaymentSimulationService {
    List<RepaymentResponseDTO> simulateAll(RepaymentRequestDTO request);

    void saveStrategy(Long userId, RepaymentResponseDTO simulationResult);

    void saveStrategies(Long userId, List<RepaymentResponseDTO> simulationResults);

    RepaymentResponseDTO getStrategy(Long userId, StrategyType strategyType);

    void deleteUserCache(Long userId);
}


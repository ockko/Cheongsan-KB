package cheongsan.domain.simulation.service;

import cheongsan.domain.simulation.dto.RepaymentRequestDTO;
import cheongsan.domain.simulation.dto.RepaymentResponseDTO;
import cheongsan.domain.simulation.dto.StrategyType;

import java.util.List;

public interface RepaymentSimulationService {
    List<RepaymentResponseDTO> simulateAll(RepaymentRequestDTO request);

    void saveStrategy(Long userId, RepaymentResponseDTO simulationResult);

    void saveStrategies(Long userId, List<RepaymentResponseDTO> simulationResults);

    RepaymentResponseDTO getStrategy(Long userId, StrategyType strategyType);

    void deleteUserCache(Long userId);

    void updateUserStrategy(Long id, String strategyName);
}


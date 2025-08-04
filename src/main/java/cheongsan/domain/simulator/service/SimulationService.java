package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.RepaymentRequestDTO;
import cheongsan.domain.simulator.dto.RepaymentResponseDTO;

import java.util.List;

public interface SimulationService {
    List<RepaymentResponseDTO> simulateAll(RepaymentRequestDTO request);
}


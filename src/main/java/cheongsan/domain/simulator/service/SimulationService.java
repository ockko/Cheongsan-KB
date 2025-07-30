package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.RepaymentRequestDTO;
import cheongsan.domain.simulator.dto.RepaymentResultDTO;

import java.util.List;

public interface SimulationService {
    List<RepaymentResultDTO> simulateAll(RepaymentRequestDTO request);
}


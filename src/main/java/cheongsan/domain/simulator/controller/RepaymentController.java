package cheongsan.domain.simulator.controller;

import cheongsan.domain.simulator.dto.RepaymentRequestDTO;
import cheongsan.domain.simulator.dto.RepaymentResponseDTO;
import cheongsan.domain.simulator.service.SimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheongsan/simulation")
public class RepaymentController {

    private final SimulationService simulationService;

    @PostMapping("/repayments")
    public ResponseEntity<List<RepaymentResponseDTO>> simulate(@RequestBody RepaymentRequestDTO request) {
        List<RepaymentResponseDTO> results = simulationService.simulateAll(request);
        return ResponseEntity.ok(results);
    }
}
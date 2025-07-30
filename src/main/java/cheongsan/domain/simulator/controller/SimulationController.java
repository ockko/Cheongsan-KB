package cheongsan.domain.simulator.controller;

import cheongsan.domain.simulator.dto.LoanAnalyzeRequestDTO;
import cheongsan.domain.simulator.dto.LoanAnalyzeResponseDTO;
import cheongsan.domain.simulator.service.LoanSimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cheongsan/simulation")
@RequiredArgsConstructor
public class SimulationController {

    private final LoanSimulationService loanSimulationService;

    @PostMapping("/loan/analyze")
    public ResponseEntity<LoanAnalyzeResponseDTO> analyzeLoan(@RequestBody LoanAnalyzeRequestDTO request) {
        LoanAnalyzeResponseDTO response = loanSimulationService.analyze(request);
        return ResponseEntity.ok(response);
    }
}

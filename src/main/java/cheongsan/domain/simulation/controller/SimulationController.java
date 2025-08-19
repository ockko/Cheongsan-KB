package cheongsan.domain.simulation.controller;

import cheongsan.domain.simulation.dto.LoanAnalyzeRequestDTO;
import cheongsan.domain.simulation.dto.LoanAnalyzeResponseDTO;
import cheongsan.domain.simulation.service.LoanSimulationService;
import cheongsan.domain.user.entity.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<LoanAnalyzeResponseDTO> analyzeLoan(
            @AuthenticationPrincipal CustomUser customUser,
            @RequestBody LoanAnalyzeRequestDTO request
    ) {
        Long userId = customUser.getUser().getId();
        LoanAnalyzeResponseDTO response = loanSimulationService.analyze(request, userId);
        return ResponseEntity.ok(response);
    }
}

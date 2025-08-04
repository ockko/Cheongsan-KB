package cheongsan.domain.simulator.controller;

import cheongsan.domain.simulator.dto.RepaymentRequestDTO;
import cheongsan.domain.simulator.dto.RepaymentResponseDTO;
import cheongsan.domain.simulator.dto.StrategyType;
import cheongsan.domain.simulator.service.RepaymentSimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheongsan/simulation/repayments")
public class RepaymentSimulationController {

    private final RepaymentSimulationService simulationService;

    @PostMapping("/analyze")
    public ResponseEntity<List<RepaymentResponseDTO>> simulate(@RequestBody RepaymentRequestDTO request) {
        List<RepaymentResponseDTO> results = simulationService.simulateAll(request);
        simulationService.saveStrategies(1L, results);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/result")
    public ResponseEntity<List<RepaymentResponseDTO>> showSimulationMain(@RequestParam("userId") Long userId) {
        List<RepaymentResponseDTO> strategies = List.of(
                simulationService.getStrategy(userId, StrategyType.TCS_RECOMMEND),
                simulationService.getStrategy(userId, StrategyType.HIGH_INTEREST_FIRST),
                simulationService.getStrategy(userId, StrategyType.SMALL_AMOUNT_FIRST),
                simulationService.getStrategy(userId, StrategyType.OLDEST_FIRST)
        );

        return ResponseEntity.ok(strategies);
    }

    @GetMapping("/detail")
    public ResponseEntity<RepaymentResponseDTO> showStrategyDetail(@RequestParam("strategyType") StrategyType strategyType,
                                                                   @RequestParam("userId") Long userId) {
        RepaymentResponseDTO strategyResult = simulationService.getStrategy(userId, strategyType);
        if (strategyResult == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(strategyResult);
    }
}
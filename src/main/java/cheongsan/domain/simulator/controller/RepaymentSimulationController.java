package cheongsan.domain.simulator.controller;

import cheongsan.domain.simulator.dto.*;
import cheongsan.domain.simulator.service.RepaymentSimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    public ResponseEntity<RepaymentResultDTO> showSimulationMain(@RequestParam("userId") Long userId, @RequestParam("monthlyAvailableAmount") BigDecimal monthlyAvailable) {
        List<RepaymentResponseDTO> strategies = List.of(
                simulationService.getStrategy(userId, StrategyType.TCS_RECOMMEND),
                simulationService.getStrategy(userId, StrategyType.HIGH_INTEREST_FIRST),
                simulationService.getStrategy(userId, StrategyType.SMALL_AMOUNT_FIRST),
                simulationService.getStrategy(userId, StrategyType.OLDEST_FIRST)
        );

        RepaymentResponseDTO tcs = simulationService.getStrategy(userId, StrategyType.TCS_RECOMMEND);
        BigDecimal existingRepaymentAmount = BigDecimal.ZERO;
        BigDecimal totalRepaymentAmount = BigDecimal.ZERO;

        LocalDate now = LocalDate.now();
        for (List<MonthlyPaymentDetailDTO> payments : tcs.getRepaymentHistory().values()) {
            for (MonthlyPaymentDetailDTO payment : payments) {
                LocalDate paymentDate = payment.getPaymentDate();
                if (paymentDate != null
                        && paymentDate.getYear() == now.getYear()
                        && paymentDate.getMonth() == now.getMonth()) {
                    existingRepaymentAmount = existingRepaymentAmount.add(payment.getPrincipal()).add(payment.getInterest());
                }
            }
        }
        totalRepaymentAmount = totalRepaymentAmount.add(existingRepaymentAmount).add(monthlyAvailable);
        RepaymentResultDTO result = new RepaymentResultDTO(existingRepaymentAmount, monthlyAvailable, totalRepaymentAmount, strategies);

        return ResponseEntity.ok(result);
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
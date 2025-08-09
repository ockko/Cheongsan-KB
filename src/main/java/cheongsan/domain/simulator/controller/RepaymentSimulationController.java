package cheongsan.domain.simulator.controller;

import cheongsan.domain.debt.dto.DebtInfoResponseDTO;
import cheongsan.domain.debt.service.DebtService;
import cheongsan.domain.simulator.dto.*;
import cheongsan.domain.simulator.service.RepaymentSimulationService;
import cheongsan.domain.user.entity.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheongsan/simulation/repayments")
public class RepaymentSimulationController {

    private final RepaymentSimulationService simulationService;
    private final DebtService debtService;

    @PostMapping("/analyze")
    public ResponseEntity<List<RepaymentResponseDTO>> simulate(@RequestParam("monthlyAvailableAmount") BigDecimal monthlyAvailableAmount, Principal principal) {
        Authentication authentication = (Authentication) principal;
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long userId = customUser.getUser().getId();

        List<LoanDTO> loans = convertToLoanDTOList(debtService.getUserDebtList(userId));
        RepaymentRequestDTO request = RepaymentRequestDTO.builder().loans(loans).monthlyAvailableAmount(monthlyAvailableAmount).build();
        List<RepaymentResponseDTO> results = simulationService.simulateAll(request);
        simulationService.saveStrategies(userId, results);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/result")
    public ResponseEntity<RepaymentResultDTO> showSimulationMain(@RequestParam("monthlyAvailableAmount") BigDecimal monthlyAvailable, Principal principal) {
        Authentication authentication = (Authentication) principal;
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long userId = customUser.getUser().getId();

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
    public ResponseEntity<RepaymentResponseDTO> showStrategyDetail(@RequestParam("strategyType") StrategyType strategyType, Principal principal) {
        Authentication authentication = (Authentication) principal;
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long userId = customUser.getUser().getId();
        RepaymentResponseDTO strategyResult = simulationService.getStrategy(userId, strategyType);
        if (strategyResult == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(strategyResult);
    }

    @DeleteMapping("/delete")
    public void deleteUserCache(Principal principal) {
        Authentication authentication = (Authentication) principal;
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long userId = customUser.getUser().getId();
        simulationService.deleteUserCache(userId);
    }


    @PutMapping("/apply")
    public ResponseEntity<String> applyStrategy(
            @RequestParam String strategyName,
            Principal principal) {

        Authentication authentication = (Authentication) principal;
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long userId = customUser.getUser().getId();
        simulationService.updateUserStrategy(userId, strategyName);
        return ResponseEntity.ok("전략이 적용되었습니다.");
    }

    private static List<LoanDTO> convertToLoanDTOList(List<DebtInfoResponseDTO> debtInfoList) {
        return debtInfoList.stream()
                .map(debt -> LoanDTO.builder()
                        .id(debt.getDebtId())
                        .loanName(debt.getDebtName())
                        .institutionType(debt.getOrganizationName())
                        .principal(debt.getCurrentBalance())
                        .interestRate(debt.getInterestRate())
                        .startDate(debt.getLoanStartDate())
                        .endDate(debt.getLoanEndDate())
                        .repaymentType(debt.getRepaymentType())
                        .prepaymentFeeRate(BigDecimal.valueOf(0.01)) // 중도상환수수료 1%로 고정
                        .paymentDate(debt.getNextPaymentDate())
                        .build())
                .collect(Collectors.toList());
    }
}
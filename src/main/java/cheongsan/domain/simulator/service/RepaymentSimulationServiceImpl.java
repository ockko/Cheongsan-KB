package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.LoanDTO;
import cheongsan.domain.simulator.dto.RepaymentRequestDTO;
import cheongsan.domain.simulator.dto.RepaymentResponseDTO;
import cheongsan.domain.simulator.dto.StrategyType;
import cheongsan.domain.simulator.service.strategy.RepaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepaymentSimulationServiceImpl implements RepaymentSimulationService {

    private final RedisTemplate<String, RepaymentResponseDTO> redisTemplate;

    private static final String STRATEGY_PREFIX = "repayment:";

    private final List<RepaymentStrategy> strategies;

    @Override
    public List<RepaymentResponseDTO> simulateAll(RepaymentRequestDTO request) {
        List<LoanDTO> loans = request.getLoans();

        return strategies.stream()
                .map(strategy -> {
                    List<LoanDTO> loanCopies = loans.stream()
                            .map(loan -> LoanDTO.builder()
                                    .id(loan.getId())
                                    .loanName(loan.getLoanName())
                                    .principal(loan.getPrincipal())
                                    .interestRate(loan.getInterestRate())
                                    .startDate(loan.getStartDate())
                                    .endDate(loan.getEndDate())
                                    .institutionType(loan.getInstitutionType())
                                    .repaymentType(loan.getRepaymentType())
                                    .prepaymentFeeRate(loan.getPrepaymentFeeRate())
                                    .paymentDate(loan.getPaymentDate())
                                    .build())
                            .collect(Collectors.toList());
                    return strategy.simulate(request, loanCopies);
                })
                .collect(Collectors.toList());

    }


    @Override
    public void saveStrategy(Long userId, RepaymentResponseDTO simulationResult) {
        String key = generateKey(userId, simulationResult.getStrategyType());
        redisTemplate.opsForValue().set(key, simulationResult, Duration.ofHours(1));
    }

    @Override
    public void saveStrategies(Long userId, List<RepaymentResponseDTO> simulationResults) {
        simulationResults.forEach(result -> saveStrategy(userId, result));
    }

    @Override
    public RepaymentResponseDTO getStrategy(Long userId, StrategyType strategyType) {
        String key = generateKey(userId, strategyType);
        return redisTemplate.opsForValue().get(key);
    }

    private String generateKey(Long userId, StrategyType strategyType) {
        return STRATEGY_PREFIX + userId + ":" + strategyType;
    }

}

package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.LoanDTO;
import cheongsan.domain.simulator.dto.RepaymentRequestDTO;
import cheongsan.domain.simulator.dto.RepaymentResponseDTO;
import cheongsan.domain.simulator.dto.StrategyType;
import cheongsan.domain.simulator.mapper.RepaymentSimulationMapper;
import cheongsan.domain.simulator.service.strategy.RepaymentStrategy;
import cheongsan.domain.user.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepaymentSimulationServiceImpl implements RepaymentSimulationService {

    private final RedisTemplate<String, RepaymentResponseDTO> repaymentStrategyRedisTemplate;
    private final List<RepaymentStrategy> strategies;
    private final RepaymentSimulationMapper repaymentSimulationMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private static final String STRATEGY_PREFIX = "repayment:";


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
        try {
            String jsonValue = objectMapper.writeValueAsString(simulationResult);
            repaymentSimulationMapper.insertCacheData(userId, key, jsonValue);
            repaymentStrategyRedisTemplate.opsForValue().set(key, simulationResult, CACHE_TTL);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("저장 실패", e);
        }
    }

    @Override
    public void saveStrategies(Long userId, List<RepaymentResponseDTO> simulationResults) {
        simulationResults.forEach(result -> saveStrategy(userId, result));
    }

    @Override
    public RepaymentResponseDTO getStrategy(Long userId, StrategyType strategyType) {
        String key = generateKey(userId, strategyType);
        RepaymentResponseDTO cachedDto = repaymentStrategyRedisTemplate.opsForValue().get(key);
        if (cachedDto != null) {
            return cachedDto;
        }
        String jsonValue = repaymentSimulationMapper.findByUserIdAndStrategyType(userId, key);
        if (jsonValue != null) {
            try {
                RepaymentResponseDTO dto = objectMapper.readValue(jsonValue, RepaymentResponseDTO.class);

                repaymentStrategyRedisTemplate.opsForValue().set(key, dto, CACHE_TTL);

                return dto;
            } catch (Exception e) {
                throw new RuntimeException("JSON 파싱 실패", e);
            }
        }
        return new RepaymentResponseDTO();
    }

    @Override
    public void deleteUserCache(Long userId) {
        repaymentSimulationMapper.deleteByUserId(userId);
    }

    @Override
    public void updateUserStrategy(Long id, String strategyName) {
        userMapper.updateStrategy(id, strategyName);
    }

    private String generateKey(Long userId, StrategyType strategyType) {
        return STRATEGY_PREFIX + userId + ":" + strategyType;
    }

}

package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.RepaymentRequestDTO;
import cheongsan.domain.simulator.dto.RepaymentResultDTO;
import cheongsan.domain.simulator.service.strategy.RepaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimulationServiceImpl implements SimulationService {

    private final List<RepaymentStrategy> strategies;

    @Override
    public List<RepaymentResultDTO> simulateAll(RepaymentRequestDTO request) {
        BigDecimal base = request.getMonthlyPayment();
        BigDecimal extra = Optional.ofNullable(request.getExtraPaymentAmount()).orElse(BigDecimal.ZERO);
        BigDecimal totalAvailable = base.add(extra);

        request.setMonthlyAvailableAmount(totalAvailable); // 전략 실행에 사용할 총 상환 가능 금액 설정

        return strategies.stream()
                .map(strategy -> strategy.simulate(request))
                .collect(Collectors.toList());
    }
}

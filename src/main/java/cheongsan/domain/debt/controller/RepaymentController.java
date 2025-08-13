package cheongsan.domain.debt.controller;


import cheongsan.common.exception.ResponseDTO;
import cheongsan.domain.simulator.dto.RepaymentResponseDTO;
import cheongsan.domain.simulator.dto.StrategyType;
import cheongsan.domain.simulator.service.RepaymentSimulationService;
import cheongsan.domain.user.entity.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cheongsan/dashboard")
@RequiredArgsConstructor
@Log4j2
public class RepaymentController {
    private final RepaymentSimulationService repaymentSimulationService;

    @GetMapping("/repaymentSummary")
    public ResponseEntity<?> getRepaymentSummary(
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Long userId = customUser.getUser().getId();
        String strategyStr = customUser.getUser().getStrategy();

        StrategyType strategy = null;
        // strategy가 null이거나 빈 문자열일 경우 예외 처리
        if (strategyStr == null || strategyStr.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO("전략이 없습니다"));
        }

        strategy = StrategyType.valueOf(strategyStr);


        RepaymentResponseDTO response = repaymentSimulationService.getStrategy(userId, strategy);
        log.info(response.toString());
        return ResponseEntity.ok(response);
    }


}

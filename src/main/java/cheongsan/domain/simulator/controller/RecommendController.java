package cheongsan.domain.simulator.controller;

import cheongsan.domain.simulator.dto.LoanProductDTO;
import cheongsan.domain.simulator.dto.LoanRecommendationRequestDTO;
import cheongsan.domain.simulator.service.LoanRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheongsan/simulation")
public class RecommendController {

    private final LoanRecommendationService recommendationService;

    @PostMapping("/recommendation")
    public ResponseEntity<?> recommend(@RequestBody LoanRecommendationRequestDTO request) {
        List<LoanProductDTO> results = recommendationService.recommendLoans(request);

        if (results.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "message", "입력하신 조건으로는 현재 대출이 어렵습니다. 연소득 대비 월 상환액을 조정해 주세요."
            ));
        }
        return ResponseEntity.ok(results);
    }
}

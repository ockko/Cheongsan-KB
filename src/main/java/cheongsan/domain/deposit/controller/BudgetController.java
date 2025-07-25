package cheongsan.domain.deposit.controller;

import cheongsan.domain.deposit.dto.BudgetLimitDTO;
import cheongsan.domain.deposit.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheongsan/dashboard/budget")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping("/recommendation")
    public ResponseEntity<BudgetLimitDTO> getBudgetLimits() {
        Long userId = 1L;

        BudgetLimitDTO resultDto = budgetService.getBudgetLimits(userId);

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }
}

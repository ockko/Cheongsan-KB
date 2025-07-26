package cheongsan.domain.deposit.controller;

import cheongsan.common.constant.ResponseMessage;
import cheongsan.common.exception.ResponseDTO;
import cheongsan.domain.deposit.dto.BudgetLimitDTO;
import cheongsan.domain.deposit.dto.DailyLimitRequestDTO;
import cheongsan.domain.deposit.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheongsan/dashboard/budget")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping("/recommendation")
    public ResponseEntity<BudgetLimitDTO> getBudgetLimits() {
        Long userId = 1L;

        BudgetLimitDTO result = budgetService.getBudgetLimits(userId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<ResponseDTO> saveFinalDailyLimit(@RequestBody DailyLimitRequestDTO request) {
        Long userId = 1L;

        budgetService.saveFinalDailyLimit(userId, request.getDailyLimit());
        ResponseDTO response = new ResponseDTO(ResponseMessage.BUDGET_LIMIT_SAVED.getMessage());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

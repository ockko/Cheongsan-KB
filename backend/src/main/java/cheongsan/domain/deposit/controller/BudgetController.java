package cheongsan.domain.deposit.controller;

import cheongsan.domain.deposit.dto.BudgetLimitDTO;
import cheongsan.domain.deposit.dto.BudgetSettingStatusDTO;
import cheongsan.domain.deposit.dto.DailyLimitRequestDTO;
import cheongsan.domain.deposit.dto.DailySpendingDTO;
import cheongsan.domain.deposit.service.BudgetService;
import cheongsan.domain.user.entity.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheongsan/dashboard/budget")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping("/recommendation")
    public ResponseEntity<BudgetLimitDTO> getBudgetLimits(@AuthenticationPrincipal CustomUser customUser) {
        Long userId = customUser.getUser().getId();
        BudgetLimitDTO result = budgetService.getBudgetLimits(userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<DailySpendingDTO> saveFinalDailyLimit(@RequestBody DailyLimitRequestDTO request, @AuthenticationPrincipal CustomUser customUser) {
        Long userId = customUser.getUser().getId();
        DailySpendingDTO updatedSpendingData = budgetService.saveFinalDailyLimit(userId, request.getDailyLimit());
        return new ResponseEntity<>(updatedSpendingData, HttpStatus.OK);
    }

    @GetMapping("/status")
    public ResponseEntity<BudgetSettingStatusDTO> getBudgetSettingStatus(@AuthenticationPrincipal CustomUser customUser) {
        Long userId = customUser.getUser().getId();
        BudgetSettingStatusDTO result = budgetService.getBudgetSettingStatus(userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

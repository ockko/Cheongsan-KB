package cheongsan.domain.deposit.controller;

import cheongsan.common.constant.ResponseMessage;
import cheongsan.common.exception.ResponseDTO;
import cheongsan.domain.deposit.dto.BudgetLimitDTO;
import cheongsan.domain.deposit.dto.BudgetSettingStatusDTO;
import cheongsan.domain.deposit.dto.DailyLimitRequestDTO;
import cheongsan.domain.deposit.service.BudgetService;
import cheongsan.domain.user.entity.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheongsan/dashboard/budget")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping("/recommendation")
    public ResponseEntity<BudgetLimitDTO> getBudgetLimits(Principal principal) {
        Authentication authentication = (Authentication) principal;
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long userId = customUser.getUser().getId();
        BudgetLimitDTO result = budgetService.getBudgetLimits(userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<ResponseDTO> saveFinalDailyLimit(@RequestBody DailyLimitRequestDTO request, Principal principal) {
        Authentication authentication = (Authentication) principal;
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long userId = customUser.getUser().getId();
        budgetService.saveFinalDailyLimit(userId, request.getDailyLimit());
        ResponseDTO response = new ResponseDTO(ResponseMessage.BUDGET_LIMIT_SAVED.getMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/status")
    public ResponseEntity<BudgetSettingStatusDTO> getBudgetSettingStatus(Principal principal) {
        Authentication authentication = (Authentication) principal;
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long userId = customUser.getUser().getId();
        BudgetSettingStatusDTO result = budgetService.getBudgetSettingStatus(userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

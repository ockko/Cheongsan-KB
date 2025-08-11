package cheongsan.domain.debt.controller;

import cheongsan.domain.debt.dto.InitialDebtDTO;
import cheongsan.domain.debt.dto.RepaymentInfoRequestDTO;
import cheongsan.domain.debt.service.DebtService;
import cheongsan.domain.user.entity.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cheongsan/initialSetup")
@RequiredArgsConstructor
public class InitialSetupController {
    private final DebtService debtService;

    @GetMapping("/loans")
    public List<InitialDebtDTO> getUserInitialLoanList(
            @RequestParam(required = false, defaultValue = "createdAtDesc") String sort,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Long userId = customUser.getUser().getId();

        return debtService.getUserInitialLoanList(userId);
    }

    @PostMapping("/loans/repayment-info")
    public void updateRepaymentInfo(
            @RequestBody RepaymentInfoRequestDTO dto,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Long userId = customUser.getUser().getId();
        debtService.updateInitialLoanInfo(userId, dto);
    }
}

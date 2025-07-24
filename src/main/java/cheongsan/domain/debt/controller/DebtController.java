package cheongsan.domain.debt.controller;

import cheongsan.domain.debt.dto.DebtDetailDTO;
import cheongsan.domain.debt.dto.DebtInfoDTO;
import cheongsan.domain.debt.service.DebtServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cheongsan/dashboard")
public class DebtController {
    private final DebtServiceImpl debtService;

    public DebtController(DebtServiceImpl debtService) {
        this.debtService = debtService;
    }

    // 추후, userId 연동 예정
    @GetMapping("/loans")
    public List<DebtInfoDTO> getUserDebtList(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "createdAtDesc") String sort
    ) {
        return debtService.getUserDebtList(userId, sort);
    }

    @GetMapping("/loans/{loanId}")
    public DebtDetailDTO getLoanDetail(@PathVariable Long loanId) {
        return debtService.getLoanDetail(loanId);
    }


}

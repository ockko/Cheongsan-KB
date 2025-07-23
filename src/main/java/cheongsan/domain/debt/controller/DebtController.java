package cheongsan.domain.debt.controller;

import cheongsan.domain.debt.dto.DebtInfoDTO;
import cheongsan.domain.debt.service.DebtServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cheongsan/dashboard")
public class DebtController {
    private final DebtServiceImpl debtService;

    public DebtController(DebtServiceImpl debtService) {
        this.debtService = debtService;
    }

    // 로그인 전, 임시 userId 전달 방식
    @GetMapping("/loans")
    public List<DebtInfoDTO> getLoansByUserId(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "createdAtDesc") String sort
    ) {
        return debtService.getDebtByUserId(userId, sort);
    }
    
}

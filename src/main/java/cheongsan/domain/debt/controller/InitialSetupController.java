package cheongsan.domain.debt.controller;

import cheongsan.domain.debt.dto.InitialDebtDTO;
import cheongsan.domain.debt.service.DebtService;
import cheongsan.domain.user.dto.DebtUpdateRequestDTO;
import cheongsan.domain.user.dto.DebtUpdateResponseDTO;
import cheongsan.domain.user.entity.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
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

    @PatchMapping("/loans/{debtAccountId}")
    public ResponseEntity<?> updateDebtAccount(
            @RequestBody DebtUpdateRequestDTO requestDTO,
            @PathVariable Long debtAccountId) {
        try {
            DebtUpdateResponseDTO responseDTO = debtService.updateDebtAccount(debtAccountId, requestDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            // 잘못된 id나 파라미터
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // 의도하지 않은 서버 에러
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 내부 오류가 발생했습니다.");
        }
    }
}

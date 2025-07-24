package cheongsan.domain.debt.controller;

import cheongsan.domain.debt.dto.request.DebtRegisterDTO;
import cheongsan.domain.debt.dto.response.DebtDetailDTO;
import cheongsan.domain.debt.dto.response.DebtInfoDTO;
import cheongsan.domain.debt.service.DebtServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(value = "/loans", consumes = "application/json")
    public ResponseEntity<String> registerDebt(@RequestBody DebtRegisterDTO dto,
                                               @RequestParam Long userId) {
        try {
            debtService.registerDebt(dto, userId);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/plain;charset=UTF-8"))
                    .body("대출 상품 추가 성공");
        } catch (RuntimeException e) {
            if (e.getMessage().equals("이미 등록된 대출 계좌입니다.")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .contentType(MediaType.parseMediaType("text/plain;charset=UTF-8"))
                        .body("이미 등록된 대출 계좌입니다.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.parseMediaType("text/plain;charset=UTF-8"))
                    .body("서버 오류가 발생했습니다.");
        }
    }


}

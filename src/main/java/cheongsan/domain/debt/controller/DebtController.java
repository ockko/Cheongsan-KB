package cheongsan.domain.debt.controller;

import cheongsan.domain.debt.dto.*;
import cheongsan.domain.debt.service.DebtService;
import cheongsan.domain.user.entity.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/cheongsan/dashboard")
@RequiredArgsConstructor
public class DebtController {
    private final DebtService debtService;

    @GetMapping("/loans")
    public ResponseEntity<List<DebtInfoResponseDTO>> getUserDebtList(
            @RequestParam(required = false, defaultValue = "createdAtDesc") String sort,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Long userId = customUser.getUser().getId();

        List<DebtInfoResponseDTO> debts = debtService.getUserDebtList(userId);
        return ResponseEntity.ok(debtService.sortDebtInfoList(debts, sort));
    }


    @GetMapping("/loans/{loanId}")
    public DebtDetailResponseDTO getLoanDetail(
            @PathVariable Long loanId,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Long userId = customUser.getUser().getId();

        return debtService.getLoanDetail(userId, loanId);
    }

    @PostMapping(value = "/loans", consumes = "application/json", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> registerDebt(
            @RequestBody DebtRegisterRequestDTO dto,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        try {
            Long userId = customUser.getUser().getId();

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

    // 대출 상환율 조회
    @GetMapping("/loans/repaymentRatio")
    public RepaymentRatioResponseDTO getRepaymentRatio(
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Long userId = customUser.getUser().getId();

        return debtService.getRepaymentRatio(userId);
    }

    // 연체 대출 정보 조회
    @GetMapping("/delinquentLoans")
    public List<DelinquentLoanResponseDTO> getDelinquentLoan(
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Long userId = customUser.getUser().getId();

        return debtService.getDelinquentLoans(userId);
    }

}

package cheongsan.domain.user.controller;

import cheongsan.common.constant.ResponseMessage;
import cheongsan.common.exception.ResponseDTO;
import cheongsan.domain.debt.service.DebtService;
import cheongsan.domain.user.dto.*;
import cheongsan.domain.user.entity.CustomUser;
import cheongsan.domain.user.service.AuthService;
import cheongsan.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cheongsan/user")
@RequiredArgsConstructor
@Log4j2
public class UserController {
    private final UserService userService;
    private final DebtService debtService;
    private final AuthService authService;

    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal CustomUser customUser) {
        try {
            Long id = customUser.getUser().getId();

            MyInfoResponseDTO myInfo = userService.getMyInfo(id);
            return ResponseEntity.ok(myInfo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO("서버 내부 오류가 발생했습니다."));
        }
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateMyProfile(
            @RequestBody UpdateMyProfileRequestDTO request,
            @AuthenticationPrincipal CustomUser customUser) {
        try {
            Long id = customUser.getUser().getId();

            UpdateMyProfileResponseDTO response = userService.updateMyProfile(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO("서버 내부 오류가 발생했습니다."));
        }
    }

    @PatchMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDTO request,
                                            @AuthenticationPrincipal CustomUser customUser) {
        try {
            Long id = customUser.getUser().getId();
            userService.changePassword(id, request);
            return ResponseEntity.ok(new ChangePasswordResponseDTO("비밀번호가 성공적으로 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(e.getMessage()));
        }
    }


    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteMyAccount(
            @RequestBody DeleteAccountRequestDTO request,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        try {
            Long id = customUser.getUser().getId();
            userService.deleteAccount(id, request);
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO("서버 내부 오류가 발생했습니다."));
        }

    }

    @GetMapping("/debt-accounts")
    public ResponseEntity<?> getMyDebt(@AuthenticationPrincipal CustomUser customUser) {
        try {
            Long id = customUser.getUser().getId();
            List<UserDebtAccountResponseDTO> accounts = userService.getUserDebtAccounts(id);
            if (accounts == null || accounts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("해당 유저의 부채 계좌를 찾을 수 없습니다.");
            }
            return ResponseEntity.ok(accounts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 에러가 발생했습니다.");
        }
    }

    @PatchMapping("/debt-accounts/{debtAccountId}")
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

    @PostMapping("/nickname")
    public ResponseEntity<?> submitNickname(@RequestBody NicknameRequestDTO request,
                                            @AuthenticationPrincipal CustomUser customUser) {
        try {

            Long id = customUser.getUser().getId();
            request.setUserId(id);

            NicknameResponseDTO response = userService.submitNickname(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(e.getMessage()));
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO("서버 내부 오류가 발생했습니다."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO> logout(@AuthenticationPrincipal CustomUser customUser) {
        if (customUser == null) {
            ResponseDTO response = new ResponseDTO(ResponseMessage.UNAUTHENTICATED_USER.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        String userId = customUser.getUser().getUserId();
        userService.logout(userId);
        ResponseDTO response = new ResponseDTO(ResponseMessage.LOGOUT_SUCCESS.getMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

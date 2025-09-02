package cheongsan.domain.user.controller;

import cheongsan.common.constant.ResponseMessage;
import cheongsan.common.exception.ResponseDTO;
import cheongsan.domain.user.dto.*;
import cheongsan.domain.user.entity.CustomUser;
import cheongsan.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cheongsan/user")
@RequiredArgsConstructor
@Log4j2
public class UserController {
    private final UserService userService;

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

    @PatchMapping("/profile/name")
    public ResponseEntity<Void> updateName(@RequestBody UpdateNicknameRequestDTO request,
                                           @AuthenticationPrincipal CustomUser customUser) {
        Long id = customUser.getUser().getId();
        userService.updateName(id, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/profile/email")
    public ResponseEntity<Void> updateEmail(@RequestBody UpdateEmailRequestDTO request,
                                            @AuthenticationPrincipal CustomUser customUser) {
        Long id = customUser.getUser().getId();
        userService.updateEmail(id, request);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/profile/changePassword")
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

    @PostMapping("/profile/verifyPassword")
    public ResponseEntity<?> verifyPassword(
            @RequestBody ChangePasswordRequestDTO request,
            @AuthenticationPrincipal CustomUser customUser) {
        try {
            Long id = customUser.getUser().getId();
            boolean isValid = userService.verifyPassword(id, request.getOldPassword());
            return ResponseEntity.ok(isValid);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ResponseDTO(e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("서버 내부 오류가 발생했습니다."));
        }
    }


    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteMyAccount(
            @RequestBody DeleteAccountRequestDTO request,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        try {
            Long id = customUser.getUser().getId();
            System.out.println("회원 탈퇴 요청 - userId: " + id + ", password: " + request.getPassword());
            userService.deleteAccount(id, request);
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(e.getMessage()));
        } catch (Exception e) {
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

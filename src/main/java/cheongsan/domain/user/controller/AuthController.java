package cheongsan.domain.user.controller;

import cheongsan.common.exception.ResponseDTO;
import cheongsan.domain.auth.dto.SocialUserInfo;
import cheongsan.domain.auth.service.NaverOAuthService;
import cheongsan.domain.user.dto.*;
import cheongsan.domain.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cheongsan/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {
    private final AuthService authService;
    private final NaverOAuthService naverOAuthService;

    @GetMapping("/checkUserId/{userId}")
    public ResponseEntity<Boolean> checkUserId(@PathVariable String userId) {
        return ResponseEntity.ok().body(authService.checkDuplicate(userId));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        try {
            SignUpResponseDTO response = authService.signUp(signUpRequestDTO);
            log.info(response.toString());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("서버 내부 오류가 발생했습니다."));
        }
    }

    @PostMapping("/findId")
    public ResponseEntity<?> findId(@RequestBody FindUserIdRequestDTO request) {
        try {
            log.info(request.toString());
            FindUserIdResponseDTO response = authService.findUserIdByEmail(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("서버 내부 오류가 발생했습니다."));
        }
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> requestPasswordReset(@RequestBody FindUserPasswordRequestDTO request) {
        try {
            FindUserPasswordResponseDTO response = authService.findUserPassword(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(e.getMessage()));
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO("서버 내부 오류가 발생했습니다."));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LogInRequestDTO request) {
        try {
            LogInResponseDTO response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("서버 내부 오류가 발생했습니다."));
        }
    }

    @PostMapping("/naver/login")
    public ResponseEntity<?> naverLogin(@RequestBody NaverLoginRequestDTO request) {
        try {
            log.info("네이버 소셜 로그인 요청 - 인증 코드 수신");

            // 1. 인증 코드로 네이버 사용자 정보 획득
            SocialUserInfo socialUserInfo = naverOAuthService.getSocialUserInfo(request.getCode());

            // 2. 사용자 정보로 로그인/회원가입 처리 후 JWT 반환
            LogInResponseDTO response = naverOAuthService.socialLogin(socialUserInfo);

            log.info("네이버 소셜 로그인 성공");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("네이버 소셜 로그인 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(e.getMessage()));
        } catch (Exception e) {
            log.error("네이버 소셜 로그인 중 서버 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("소셜 로그인 중 오류가 발생했습니다."));
        }
    }


}

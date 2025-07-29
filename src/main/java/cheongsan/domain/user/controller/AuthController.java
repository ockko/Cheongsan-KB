package cheongsan.domain.user.controller;

import cheongsan.common.exception.ResponseDTO;
import cheongsan.domain.user.dto.*;
import cheongsan.domain.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cheongsan/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {
    private final AuthService authService;
    
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO("서버 내부 오류가 발생했습니다."));
        }
    }

}

package cheongsan.domain.user.controller;

import cheongsan.common.exception.ResponseDTO;
import cheongsan.domain.user.dto.SignUpRequestDTO;
import cheongsan.domain.user.dto.SignUpResponseDTO;
import cheongsan.domain.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cheongsan/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {
    private final AuthService authService;


    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        log.info("signUpRequestDTO: {}", signUpRequestDTO);
        System.out.println("뭐가 문제야");
        try {
            log.info("signUp request: {}", signUpRequestDTO);
            SignUpResponseDTO response = authService.signUp(signUpRequestDTO);
            log.info(response.toString());

            // 회원가입 성공: 200 OK + id, userId 반환(JSON)
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // 중복 아이디/이메일 등 검증 실패: 400 Bad Request + 에러 메시지 반환(JSON)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(e.getMessage()));
        } catch (RuntimeException e) {
            // 기타 예기치 못한 에러: 500 ERROR
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("서버 내부 오류가 발생했습니다.anjrk answpdi"));
        }
    }


}

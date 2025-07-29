package cheongsan.domain.user.controller;

import cheongsan.common.exception.ResponseDTO;
import cheongsan.domain.user.dto.MyInfoResponseDTO;
import cheongsan.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cheongsan/user")
@RequiredArgsConstructor
@Log4j2
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile() {
        try {
            String userId = "antehyun4880";

            MyInfoResponseDTO myInfo = userService.getMyInfo(userId);
            return ResponseEntity.ok(myInfo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO("서버 내부 오류가 발생했습니다."));
        }
    }

}

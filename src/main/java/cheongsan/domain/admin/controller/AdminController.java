package cheongsan.domain.admin.controller;

import cheongsan.common.exception.ResponseDTO;
import cheongsan.domain.admin.dto.AdminUserListDTO;
import cheongsan.domain.admin.service.AdminService;
import cheongsan.domain.codef.service.CodefSyncService;
import cheongsan.domain.user.entity.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cheongsan/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final AdminService adminService;
    private final CodefSyncService codefSyncService;

    @GetMapping("/users")
    public List<AdminUserListDTO> getUserList() {
        return adminService.getUserList();
    }

    @DeleteMapping("/users")
    public ResponseEntity<Map<String, Object>> deleteUser(
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Long userId = customUser.getUser().getId();
        adminService.deleteUser(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "회원이 정상적으로 삭제되었습니다.");
        response.put("userId", userId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sync/batch/all")
    public ResponseEntity<?> batchSyncAllUsers() {
        try {
            log.info("전체 사용자 배치 동기화 요청");

            codefSyncService.batchSyncAllUsers();

            Map<String, String> response = new HashMap<>();
            response.put("message", "전체 사용자 배치 동기화가 완료되었습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("전체 사용자 배치 동기화 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("전체 사용자 배치 동기화에 실패했습니다: " + e.getMessage()));
        }
    }

}

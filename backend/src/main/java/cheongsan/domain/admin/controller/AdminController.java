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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cheongsan/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final AdminService adminService;
    private final CodefSyncService codefSyncService;

    @GetMapping("/users")
    public List<AdminUserListDTO> getUserList() {
        return adminService.getUserList();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUser admin
    ) {
        Long me = admin.getUser().getId();
        if (me.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
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

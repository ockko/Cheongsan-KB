package cheongsan.domain.admin.controller;

import cheongsan.domain.admin.dto.AdminUserListDTO;
import cheongsan.domain.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cheongsan/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    public List<AdminUserListDTO> getUserList() {
        return adminService.getUserList();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "회원이 정상적으로 삭제되었습니다.");
        response.put("userId", userId);

        return ResponseEntity.ok(response);
    }

}

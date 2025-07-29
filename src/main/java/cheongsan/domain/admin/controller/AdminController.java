package cheongsan.domain.admin.controller;

import cheongsan.domain.admin.dto.AdminUserListDTO;
import cheongsan.domain.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cheongsan/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    public List<AdminUserListDTO> getUserList() {
        return adminService.getUserList();
    }
}

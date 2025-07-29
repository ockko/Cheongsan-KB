package cheongsan.domain.admin.service;

import cheongsan.domain.admin.dto.AdminUserListDTO;
import cheongsan.domain.admin.entity.AdminUser;
import cheongsan.domain.admin.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;

    @Override
    public List<AdminUserListDTO> getUserList() {
        List<AdminUser> users = adminMapper.getAllUsers();

        return users.stream()
                .map(AdminUserListDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        adminMapper.deleteUserById(userId);
    }
}

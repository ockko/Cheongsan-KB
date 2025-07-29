package cheongsan.domain.admin.service;

import cheongsan.domain.admin.dto.AdminUserListDTO;

import java.util.List;

public interface AdminService {
    List<AdminUserListDTO> getUserList();
}

package cheongsan.domain.admin.service;

import cheongsan.domain.admin.dto.AdminUserListDTO;

import java.util.List;

public interface AdminService {

    // 전체 회원 조회
    List<AdminUserListDTO> getUserList();

    // 특정 회원 삭제
    void deleteUser(Long userId);
}

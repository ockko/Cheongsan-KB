package cheongsan.domain.admin.dto;

import cheongsan.domain.admin.entity.AdminUser;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AdminUserListDTO {
    private Long id;
    private String userId;
    private String nickname;
    private LocalDate createdAt;
    private String role;

    public static AdminUserListDTO fromEntity(AdminUser user) {
        return new AdminUserListDTO(
                user.getId(),
                user.getUserId(),
                user.getNickname(),
                user.getCreatedAt(),
                user.getRole()
        );
    }
}

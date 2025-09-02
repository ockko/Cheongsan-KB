package cheongsan.domain.admin.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class AdminUser {
    private Long id;
    private String userId;
    private String nickname;
    private LocalDate createdAt;
    private String role;

}

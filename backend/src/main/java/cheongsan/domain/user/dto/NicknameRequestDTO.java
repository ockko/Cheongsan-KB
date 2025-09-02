package cheongsan.domain.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NicknameRequestDTO {
    private Long userId;
    private String nickname;
}

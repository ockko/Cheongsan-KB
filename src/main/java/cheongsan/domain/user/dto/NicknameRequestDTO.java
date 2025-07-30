package cheongsan.domain.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NicknameRequestDTO {
    private String userId;
    private String nickname;
}

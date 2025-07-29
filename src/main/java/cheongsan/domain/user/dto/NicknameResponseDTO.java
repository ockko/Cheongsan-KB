package cheongsan.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NicknameResponseDTO {
    private String nickname;
    private String message;
}

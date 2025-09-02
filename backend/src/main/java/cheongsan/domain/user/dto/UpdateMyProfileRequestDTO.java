package cheongsan.domain.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateMyProfileRequestDTO {
    private String nickname;
    private String email;
}

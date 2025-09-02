package cheongsan.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UpdateMyProfileResponseDTO {
    private String message;
    private String nickname;
    private String email;
}

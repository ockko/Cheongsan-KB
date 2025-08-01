package cheongsan.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogInResponseDTO {
    private Long id;
    private String accessToken;
    private String refreshToken;
}

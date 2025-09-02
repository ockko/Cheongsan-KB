package cheongsan.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpResponseDTO {
    private Long id;
    private String userId;
}

package cheongsan.domain.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FindUserPasswordRequestDTO {
    private String userId;
    private String email;
}

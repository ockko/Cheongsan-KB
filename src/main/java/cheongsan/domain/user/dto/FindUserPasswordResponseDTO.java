package cheongsan.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindUserPasswordResponseDTO {
    private String tempPassword;

}

package cheongsan.domain.user.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class SignUpRequestDTO {
    private String userId;
    private String password;
    private String email;
}

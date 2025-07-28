package cheongsan.domain.user.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDTO {
    private String userId;
    private String password;
    private String email;
}

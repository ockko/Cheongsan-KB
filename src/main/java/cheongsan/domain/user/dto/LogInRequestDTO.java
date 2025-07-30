package cheongsan.domain.user.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LogInRequestDTO {
    private String userId;
    private String password;
}

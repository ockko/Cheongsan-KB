package cheongsan.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MyInfoResponseDTO {
    private Long id;
    private String userId;
    private String nickname;
    private String email;

    
}
package cheongsan.domain.user.dto;

import cheongsan.domain.user.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private Long recommendedProgramId;
    private String nickname;
    private String email;
    private BigDecimal dailyLimit;
    private String status;

    public UserDTO(User user) {
        id = user.getId();
        recommendedProgramId = user.getRecommendedProgramId();
        nickname = user.getNickname();
        email = user.getEmail();
        dailyLimit = user.getDailyLimit();
        status = user.getStatus();
    }

}

package cheongsan.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private Long recommendedProgramId;
    private String nickname;
    private String email;
    private BigDecimal dailyLimit;
    private String status;

}

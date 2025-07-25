package cheongsan.domain.user.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class User {
    private Long id;
    private Long recommendedProgramId;
    private String nickname;
    private String email;
    private BigDecimal dailyLimit;
    private LocalDateTime dailyLimitDate;
    private String status;

}

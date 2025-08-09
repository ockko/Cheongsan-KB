package cheongsan.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private Long recommendedProgramId;
    private String connectedId;
    private String nickname;
    private String userId;
    private String password;
    private String email;
    private LocalDateTime createdAt;
    private BigDecimal dailyLimit;
    private LocalDateTime dailyLimitDate;
    private String status;
    private String role;
    private String strategy;

}

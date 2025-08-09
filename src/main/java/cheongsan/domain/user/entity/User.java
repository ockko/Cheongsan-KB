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
    private String password; // 네이버 사용자는 NULL (네이버에서 인증하므로)
    private String email;
    private LocalDateTime createdAt;
    private BigDecimal dailyLimit;
    private LocalDateTime dailyLimitDate;
    private String status;
    private String role;
    private String strategy;

    // 네이버 로그인 관련 필드
    private String naverId;         // 네이버 고유 ID
    private String accountType;     // GENERAL, NAVER
}

package cheongsan.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NaverUserInfo {
    private String id;
    private String email;
    private String name;
    private String nickname;

    // 네이버 API 응답 구조에 맞춰 변환
    public static NaverUserInfo fromNaverResponse(Map<String, Object> response) {
        Map<String, Object> account = (Map<String, Object>) response.get("response");
        return new NaverUserInfo(
                (String) account.get("id"),
                (String) account.get("email"),
                (String) account.get("name"),
                (String) account.get("nickname")
        );
    }
}

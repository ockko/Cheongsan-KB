package cheongsan.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NaverUserInfoAdapter implements SocialUserInfo {
    private NaverUserInfo naverUserInfo;

    @Override
    public String getProviderId() {
        return naverUserInfo.getId();
    }

    @Override
    public String getEmail() {
        return naverUserInfo.getEmail();
    }

    @Override
    public String getNickname() {
        return naverUserInfo.getNickname();
    }

    @Override
    public String getProvider() {
        return "NAVER";
    }
}

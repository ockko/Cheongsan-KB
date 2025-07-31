package cheongsan.domain.auth.dto;

public interface SocialUserInfo {
    String getProviderId();

    String getEmail();

    String getNickname();

    String getProvider();
}

package cheongsan.domain.auth.service;

import cheongsan.domain.auth.dto.NaverUserInfo;
import cheongsan.domain.auth.dto.NaverUserInfoAdapter;
import cheongsan.domain.auth.dto.SocialUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class NaverOAuthService {
    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Value("${naver.redirect-uri}")
    private String redirectUri;

    @Value("${naver.token-url}")
    private String tokenUrl;

    @Value("${naver.profile-url}")
    private String profileUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 인증 코드로 네이버 액세스 토큰 요청
     */
    public String getAccessToken(String authCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", authCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            Map<String, Object> responseBody = response.getBody();
            return (String) responseBody.get("access_token");
        } catch (Exception e) {
            log.error("네이버 액세스 토큰 요청 실패", e);
            throw new RuntimeException("네이버 인증에 실패했습니다.");
        }
    }

    /**
     * 액세스 토큰으로 네이버 사용자 정보 요청
     */
    public NaverUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(profileUrl, HttpMethod.GET, request, Map.class);
            Map<String, Object> responseBody = response.getBody();
            return NaverUserInfo.fromNaverResponse(responseBody);
        } catch (Exception e) {
            log.error("네이버 사용자 정보 요청 실패", e);
            throw new RuntimeException("네이버 사용자 정보 조회에 실패했습니다.");
        }
    }

    /**
     * 인증 코드 → 사용자 정보 (원스톱 메서드)
     */
    public SocialUserInfo getSocialUserInfo(String authCode) {
        String accessToken = getAccessToken(authCode);
        NaverUserInfo naverUserInfo = getUserInfo(accessToken);
        return new NaverUserInfoAdapter(naverUserInfo);
    }
}

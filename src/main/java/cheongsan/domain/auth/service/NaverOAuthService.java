package cheongsan.domain.auth.service;

import cheongsan.domain.auth.dto.NaverUserInfo;
import cheongsan.domain.auth.dto.NaverUserInfoAdapter;
import cheongsan.domain.auth.dto.SocialUserInfo;
import cheongsan.domain.user.dto.LogInResponseDTO;
import cheongsan.domain.user.dto.NaverLoginRequestDTO;
import cheongsan.domain.user.service.AuthService;
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
    private final AuthService authService;

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
    public NaverLoginRequestDTO getAccessToken(String authCode) {
        log.info("=== 네이버 액세스 토큰 요청 시작 ===");
        log.info("authCode: {}", authCode);
        log.info("clientId: {}", clientId);
        log.info("redirectUri: {}", redirectUri);
        log.info("tokenUrl: {}", tokenUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", authCode);

        log.info("요청 파라미터: {}", params);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            log.info("=== 네이버 토큰 응답 ===");
            log.info("HTTP Status: {}", response.getStatusCode());
            log.info("Response Body: {}", responseBody);

            // 에러 응답 체크
            if (responseBody.containsKey("error")) {
                log.error("네이버 토큰 요청 에러: {}", responseBody.get("error"));
                log.error("에러 설명: {}", responseBody.get("error_description"));
                throw new RuntimeException("네이버 토큰 요청 실패: " + responseBody.get("error_description"));
            }

            String accessToken = (String) responseBody.get("access_token");
            if (accessToken == null) {
                log.error("access_token이 null입니다. 전체 응답: {}", responseBody);
                throw new RuntimeException("네이버에서 access_token을 받지 못했습니다");
            }

            log.info("액세스 토큰 획득 성공: {}", accessToken.substring(0, Math.min(accessToken.length(), 10)) + "...");

            return NaverLoginRequestDTO.builder()
                    .code(accessToken)
                    .build();

        } catch (Exception e) {
            log.error("네이버 액세스 토큰 요청 실패", e);
            throw new RuntimeException("네이버 인증에 실패했습니다: " + e.getMessage());
        }

    }

    /**
     * 액세스 토큰으로 네이버 사용자 정보 요청
     */
    public NaverUserInfo getUserInfo(String accessToken) {
        log.info("=== 네이버 사용자 정보 요청 시작 ===");
        log.info("accessToken: {}...", accessToken.substring(0, Math.min(accessToken.length(), 10)));
        log.info("profileUrl: {}", profileUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        log.info("요청 헤더: Authorization = Bearer {}...", accessToken.substring(0, Math.min(accessToken.length(), 10)));

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(profileUrl, HttpMethod.GET, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            log.info("=== 네이버 사용자 정보 응답 ===");
            log.info("HTTP Status: {}", response.getStatusCode());
            log.info("Response Body: {}", responseBody);

            return NaverUserInfo.fromNaverResponse(responseBody);
        } catch (Exception e) {
            log.error("네이버 사용자 정보 요청 실패", e);
            if (e.getMessage().contains("401")) {
                log.error("401 에러 - 액세스 토큰이 유효하지 않습니다: {}", accessToken);
            }
            throw new RuntimeException("네이버 사용자 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 인증 코드 → 사용자 정보 (원스톱 메서드)
     */
    public SocialUserInfo getSocialUserInfo(String authCode) {
        String accessToken = getAccessToken(authCode).getCode();
        NaverUserInfo naverUserInfo = getUserInfo(accessToken);
        return new NaverUserInfoAdapter(naverUserInfo);
    }

    public LogInResponseDTO socialLogin(SocialUserInfo socialUserInfo) {
        return authService.naverSignUpOrLogin(socialUserInfo);
    }

}

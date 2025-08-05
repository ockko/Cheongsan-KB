package cheongsan.common.security.handler;

import cheongsan.common.security.util.JsonResponse;
import cheongsan.common.security.util.JwtProcessor;
import cheongsan.domain.user.dto.LogInResponseDTO;
import cheongsan.domain.user.entity.CustomUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtProcessor jwtProcessor;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final long REFRESH_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 14;

    @Autowired
    public LoginSuccessHandler(
            JwtProcessor jwtProcessor,
            @Qualifier("redisTemplateForToken") RedisTemplate<String, Object> redisTemplate
    ) {
        this.jwtProcessor = jwtProcessor;
        this.redisTemplate = redisTemplate;
    }

    private LogInResponseDTO makeLogInResponse(CustomUser user) {
        String userId = user.getUser().getUserId();
        String accessToken = jwtProcessor.generateAccessToken(userId);
        String refreshToken = jwtProcessor.generateRefreshToken(userId);

        redisTemplate.opsForValue().set(
                "RT:" + userId,
                refreshToken,
                REFRESH_TOKEN_VALIDITY_SECONDS,
                TimeUnit.SECONDS
        );

        return new LogInResponseDTO(user.getUser().getId(), accessToken, refreshToken);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomUser user = (CustomUser) authentication.getPrincipal();
        LogInResponseDTO result = makeLogInResponse(user);
        JsonResponse.send(response, result);
    }
}

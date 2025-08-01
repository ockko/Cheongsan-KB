package cheongsan.common.security.handler;

import cheongsan.common.security.util.JsonResponse;
import cheongsan.common.security.util.JwtProcessor;
import cheongsan.domain.user.dto.LogInResponseDTO;
import cheongsan.domain.user.entity.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtProcessor jwtProcessor;

    private LogInResponseDTO makeLogInResponse(CustomUser user) {
        String username = user.getUsername();
        String accessToken = jwtProcessor.generateAccessToken(username);
        String refreshToken = jwtProcessor.generateRefreshToken(username);
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

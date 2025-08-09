package cheongsan.common.util;

import cheongsan.domain.user.entity.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Log4j2
public class ExtractUserIdUtil {
    public Long extractUserId(Principal principal) {
        if (principal == null) {
            log.warn("Principal is null.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required.");
        }
        try {
            Authentication authentication = (Authentication) principal;
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            return customUser.getUser().getId();
        } catch (Exception e) {
            log.error("Failed to extract user ID from principal.", e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user authentication.");
        }
    }

}

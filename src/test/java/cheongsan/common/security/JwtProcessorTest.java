package cheongsan.common.security;

import cheongsan.common.config.RootConfig;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class, SecurityConfig.class})
@WebAppConfiguration
@Log4j2
class JwtProcessorTest {
    @Autowired
    JwtProcessor jwtProcessor;

    @Test
    void generateToken() {
        String username = "user0";
        String token = jwtProcessor.generateToken(username);
        log.info(token);
        assertNotNull(token);
    }

    @Test
    void getUsername() {
        String token = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ1c2VyMCIsImlhdCI6MTc1NDAxNTA0OCwiZXhwIjoxNzU0MDE1MTY4fQ.IcHqm7YxR6skUT1LE0toYGrb8HJzS9s_DVpw3p2lm0hbj7pT2P1RxnK95-1-4Wux";

        String username = jwtProcessor.getUsername(token);
        log.info(username);
        assertNotNull(username);
    }

    @Test
    void validateToken() {
        String token = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ1c2VyMCIsImlhdCI6MTc1NDAxNTA0OCwiZXhwIjoxNzU0MDE1MTY4fQ.IcHqm7YxR6skUT1LE0toYGrb8HJzS9s_DVpw3p2lm0hbj7pT2P1RxnK95-1-4Wux";

        boolean isValid = jwtProcessor.validateToken(token); // 2분 경과 후면 예외 발생
        log.info(isValid);
        assertTrue(isValid); // 2분전이면 true
    }
}
package cheongsan.common.security.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Log4j2
public class PasswordEncoderTest {
    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode("1234");

        log.info("암호화된 비밀번호: " + hashedPassword);
        // 실행할 때마다 결과는 달라지지만, 모두 "1234"와 일치합니다.
        // 예시 출력: $2a$10$EblZqN0k4fDEb.E4k3y2.OANc42R.gV2n3J5S.YOj0un5a5.5e9.a
    }
}

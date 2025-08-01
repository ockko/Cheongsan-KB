package cheongsan.common.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProcessor {
    static private final long ACCESS_TOKEN_VALID_MILISECOND = 1000L * 60 * 2; // 2분
    static private final long REFRESH_TOKEN_VALID_MILISECOND = 1000L * 60 * 2 * 2; // 4분

//    static private final long ACCESS_TOKEN_VALID_MILISECOND = 1000L * 60 * 60; // 1시간 -- 운영시 사용
//    static private final long REFRESH_TOKEN_VALID_MILISECOND = 1000L * 60 * 60 * 24 * 14; // 2주 -- 운영시 사용

    private final String secretKey = "충분히 긴 임의의(랜덤한) 비밀키 문자열 배정";
    private final Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

//    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // -- 운영시 사용

    public String generateAccessToken(String subject) {
        return createToken(subject, ACCESS_TOKEN_VALID_MILISECOND);
    }

    public String generateRefreshToken(String subject) {
        return createToken(subject, REFRESH_TOKEN_VALID_MILISECOND);
    }

    private String createToken(String subject, long validityInMilliseconds) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        return true;
    }
}

package cheongsan.common.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * CODEF API RSA 암호화 유틸리티
 */
@Log4j2
@Component
public class CodefRSAUtil {
    // CODEF에서 발급받은 공개키
    @Value("${codef.public-key}")
    private String publicKey;

    /**
     * 비밀번호 RSA 암호화 (non-static 메서드로 변경)
     */
    public String encryptPassword(String password) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKeyObj = keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKeyObj);

            byte[] encryptedBytes = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);

        } catch (Exception e) {
            log.error("비밀번호 RSA 암호화 실패", e);
            throw new RuntimeException("비밀번호 암호화 실패: " + e.getMessage(), e);
        }
    }
}

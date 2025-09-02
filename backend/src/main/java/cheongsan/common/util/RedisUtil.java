package cheongsan.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;

    public void set(String key, String value, int minutes) {
        redisTemplate.opsForValue().set(key, value, minutes);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public Long checkExpired(String key) {
        Long ttl = redisTemplate.getExpire(key);
        return ttl;
    }

    public long countResumeKeysWithPrefix() {
        ScanOptions options = ScanOptions.scanOptions()
                .match("resume:temp:" + "*")
                .count(1000) // 한 번에 검색할 키 수
                .build();

        long count = 0;
        try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory()
                .getConnection()
                .scan(options)) {

            while (cursor.hasNext()) {
                cursor.next();
                count++;
            }
        } catch (Exception e) {
            throw new RuntimeException("Redis scan error", e);
        }

        return count;
    }

}
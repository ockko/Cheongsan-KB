package cheongsan.common.config;

import cheongsan.domain.simulator.dto.RepaymentResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${redis.url}")
    private String redisUrl;
    @Value("${redis.password}")
    private String redisPassword;

    @Bean
    public RedisTemplate<String, Object> redisTemplateForToken() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, RepaymentResponseDTO> repaymentStrategyRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RepaymentResponseDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<RepaymentResponseDTO> serializer = new Jackson2JsonRedisSerializer<>(RepaymentResponseDTO.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules(); // JavaTimeModule 등 자동 등록
        serializer.setObjectMapper(mapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        // serializer 설정도 필요시 추가 가능

        // 일반적인 key:value의 경우 시리얼라이저
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        // Hash를 사용할 경우 시리얼라이저
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        // 모든 경우
        template.setDefaultSerializer(new StringRedisSerializer());


        return template;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisUrl);
        config.setPort(6379);
        config.setPassword(redisPassword);
        return new LettuceConnectionFactory(config);
    }
}

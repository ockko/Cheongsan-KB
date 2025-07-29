package cheongsan.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// CORS 전역 설정 클래스
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 모든 URL에 대해
                .allowedOrigins("http://127.0.0.1:5500") // 허용할 프론트엔드 주소 - 필요에 따라 변경하기
                .allowedMethods("*")  // GET, POST, PATCH 등 모든 메서드 허용
                .allowedHeaders("*")  // 모든 헤더 허용
                .allowCredentials(true); // 인증정보 전송 허용 (필요시)
    }
}

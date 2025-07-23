package cheongsan.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@EnableWebMvc // Spring MVC 기능 활성화
@ComponentScan(basePackages = {
        "cheongsan.domain"
})
public class ServletConfig implements WebMvcConfigurer {

    // 정적 자원 처리 설정 (CSS, JS, 이미지 등)
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/resources/**") // 요청 URL 경로
                .addResourceLocations("/resources/"); // 실제 리소스 위치
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("/resources/favicon.ico");
    }

    // JSP ViewResolver 설정
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver bean = new InternalResourceViewResolver();

        bean.setViewClass(JstlView.class); // JSTL 지원 뷰
        bean.setPrefix("/WEB-INF/views/");
        bean.setSuffix(".jsp");

        registry.viewResolver(bean); // 등록
    }

}

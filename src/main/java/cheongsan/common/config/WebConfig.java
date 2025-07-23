package cheongsan.common.config;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

public class WebConfig extends AbstractAnnotationConfigDispatcherServletInitializer {
    final String LOCATION = "c:/upload";                      // 업로드 파일 저장 경로
    final long MAX_FILE_SIZE = 1024 * 1024 * 10L;             // 10MB (파일 하나의 최대 크기)
    final long MAX_REQUEST_SIZE = 1024 * 1024 * 20L;          // 20MB (전체 요청 최대 크기)
    final int FILE_SIZE_THRESHOLD = 1024 * 1024 * 5;          // 5MB (임계값: 메모리 임시 저장)

    // 루트 어플리케이션 설정 (서비스, DAO, DB 등)
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{RootConfig.class};
    }

    // 서블릿 어플리케이션 설정 (Controller, ViewResolver 등)
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{ServletConfig.class};
    }

    // DispatcherServlet의 URL 매핑
    @Override
    protected String[] getServletMappings() {
        // 모든 요청을 DispatcherServlet이 처리
        return new String[]{"/"};
    }

    // 한글 POST 요청 인코딩 필터 설정
    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);

        return new Filter[]{characterEncodingFilter};
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        // 존재하지 않는 URL 요청시 404 응답이 아닌 예외 발생시킴
        registration.setInitParameter("throwExceptionIfNoHandlerFound", "true");

        MultipartConfigElement multipartConfig = new MultipartConfigElement(
                LOCATION,
                MAX_FILE_SIZE,
                MAX_REQUEST_SIZE,
                FILE_SIZE_THRESHOLD
        );

        // multipart 파일 업로드 설정
        registration.setMultipartConfig(multipartConfig);
    }
}

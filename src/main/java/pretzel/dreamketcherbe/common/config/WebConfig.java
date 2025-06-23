package pretzel.dreamketcherbe.common.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pretzel.dreamketcherbe.domain.auth.config.AdminArgumentResolver;
import pretzel.dreamketcherbe.domain.auth.config.AuthArgumentResolver;
import pretzel.dreamketcherbe.domain.auth.config.AuthInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AuthArgumentResolver authArgumentResolver;
    private final AdminArgumentResolver adminArgumentResolver;

    /**
     * 커스텀 인증 및 관리자 인자 리졸버를 Spring MVC에 등록합니다.
     *
     * @param resolvers 컨트롤러 메서드 파라미터를 처리할 인자 리졸버 목록
     */
    @Override
    public void addArgumentResolvers(
        List<HandlerMethodArgumentResolver> resolvers
    ) {
        resolvers.add(authArgumentResolver);
        resolvers.add(adminArgumentResolver);
    }

    /**
     * 모든 "/api/v1/**" 경로에 대해 인증 인터셉터를 등록합니다.
     *
     * 인증이 필요한 API 요청에 대해 {@code authInterceptor}가 적용되도록 인터셉터 레지스트리에 추가합니다.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
            .addPathPatterns("/api/v1/**");
    }
}

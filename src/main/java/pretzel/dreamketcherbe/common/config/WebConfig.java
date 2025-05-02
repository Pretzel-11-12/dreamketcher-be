package pretzel.dreamketcherbe.common.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pretzel.dreamketcherbe.domain.auth.config.AuthArgumentResolver;
import pretzel.dreamketcherbe.domain.auth.config.AuthInterceptor;
import pretzel.dreamketcherbe.domain.auth.config.RequesterArgumentResolver;
import pretzel.dreamketcherbe.domain.auth.config.RequesterInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AuthArgumentResolver authArgumentResolver;
    private final RequesterArgumentResolver requesterArgumentResolver;
    private final RequesterInterceptor requesterInterceptor;

    /**
     * Spring MVC에 사용자 정의 인자 리졸버를 추가합니다.
     *
     * 인증 정보와 요청자 정보를 처리하는 {@code AuthArgumentResolver}와 {@code RequesterArgumentResolver}를 인자 리졸버 목록에 등록하여, 컨트롤러 메서드에서 해당 정보를 주입받을 수 있도록 합니다.
     *
     * @param resolvers 인자 리졸버 목록
     */
    @Override
    public void addArgumentResolvers(
        List<HandlerMethodArgumentResolver> resolvers
    ) {
        resolvers.add(authArgumentResolver);
        resolvers.add(requesterArgumentResolver);
    }

    /**
     * API 요청 경로(`/api/v1/**`)에 대해 인증 및 요청자 관련 인터셉터를 등록합니다.
     *
     * 인증 및 요청자 처리를 위한 인터셉터가 Spring MVC 요청 처리 파이프라인에 적용됩니다.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
            .addPathPatterns("/api/v1/**");
        registry.addInterceptor(requesterInterceptor)
            .addPathPatterns("/api/v1/**");
    }
}

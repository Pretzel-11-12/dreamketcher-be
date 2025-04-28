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

    @Override
    public void addArgumentResolvers(
        List<HandlerMethodArgumentResolver> resolvers
    ) {
        resolvers.add(authArgumentResolver);
        resolvers.add(requesterArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
            .addPathPatterns("/api/v1/**");
        registry.addInterceptor(requesterInterceptor)
            .addPathPatterns("/api/v1/**");
    }
}

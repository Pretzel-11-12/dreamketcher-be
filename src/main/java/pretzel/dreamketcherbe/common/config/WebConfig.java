package pretzel.dreamketcherbe.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pretzel.dreamketcherbe.auth.config.AuthArgumentResolver;
import pretzel.dreamketcherbe.auth.config.AuthInterceptor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AuthArgumentResolver authArgumentResolver;

    @Override
    public void addArgumentResolvers(
        List<HandlerMethodArgumentResolver> resolvers
    ) {
        resolvers.add(authArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
            .addPathPatterns("/api/v1/member/me");
    }
}

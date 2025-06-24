package pretzel.dreamketcherbe.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import pretzel.dreamketcherbe.common.annotation.Admin;

/**
 * @Admin 어노테이션을 사용하는 테스트를 위한 구성 클래스
 */
@Configuration
public class AdminArgumentResolverTestConfig {

    /**
     * 테스트용 AdminArgumentResolver를 제공합니다.
     * 테스트 실행 시 Admin 주입 값으로 27L을 반환합니다.
     */
    @Bean
    @Primary
    public HandlerMethodArgumentResolver adminArgumentResolver() {
        return new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.getParameterType().equals(Long.class) &&
                    parameter.hasParameterAnnotation(Admin.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return 27L; // 테스트용 관리자 ID
            }
        };
    }
}

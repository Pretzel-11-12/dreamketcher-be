package pretzel.dreamketcherbe.domain.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import pretzel.dreamketcherbe.common.annotation.Requester;
import pretzel.dreamketcherbe.domain.auth.dto.RequestInfo;
import pretzel.dreamketcherbe.domain.auth.service.internal.RequesterContext;

@Component
@RequiredArgsConstructor
public class RequesterArgumentResolver implements HandlerMethodArgumentResolver {

    private final RequesterContext requesterContext;

    /**
     * 메서드 파라미터가 {@code RequestInfo} 타입이고 {@link Requester} 애노테이션이 적용되어 있는지 여부를 반환합니다.
     *
     * @param parameter 검사할 메서드 파라미터
     * @return 해당 파라미터가 지원 대상이면 {@code true}, 아니면 {@code false}
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(RequestInfo.class) &&
            parameter.hasParameterAnnotation(Requester.class);
    }

    /**
     * 현재 요청자의 정보를 반환하여 컨트롤러 메서드의 인자로 주입합니다.
     *
     * @return 현재 요청자의 RequestInfo 객체
     */
    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) {
        return requesterContext.getRequester();
    }
}

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

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(RequestInfo.class) &&
            parameter.hasParameterAnnotation(Requester.class);
    }

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

package pretzel.dreamketcherbe.domain.auth.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import pretzel.dreamketcherbe.common.annotation.Admin;
import pretzel.dreamketcherbe.domain.auth.exception.AuthException;
import pretzel.dreamketcherbe.domain.auth.exception.AuthExceptionType;
import pretzel.dreamketcherbe.domain.auth.service.internal.AuthContext;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.Role;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthContext authContext;
    private final MemberRepository memberRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("AdminArgumentResolver supportsParameter: {}", parameter.getParameterType());
        return parameter.getParameterType().equals(Long.class) &&
            parameter.hasParameterAnnotation(Admin.class);
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory) {

        log.info("AdminArgumentResolver resolveArgument: {}", parameter.getParameterType());

        Long memberId = authContext.getMemberId();

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new AuthException(AuthExceptionType.UNAUTHORIZED));

        if (member.getRole() != Role.ADMIN) {
            throw new AuthException(AuthExceptionType.FORBIDDEN);
        }

        return memberId;
    }
}

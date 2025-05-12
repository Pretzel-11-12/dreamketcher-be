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

    /**
     * 메서드 파라미터가 {@code Long} 타입이며 {@link Admin} 애노테이션이 적용되어 있는지 확인합니다.
     *
     * @param parameter 검사할 메서드 파라미터
     * @return 해당 파라미터가 지원되면 {@code true}, 아니면 {@code false}
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("AdminArgumentResolver supportsParameter: {}", parameter.getParameterType());
        return parameter.getParameterType().equals(Long.class) &&
            parameter.hasParameterAnnotation(Admin.class);
    }

    /**
     * 현재 인증된 사용자가 관리자 권한을 가지고 있는지 확인하고, 관리자일 경우 해당 사용자의 ID를 반환합니다.
     *
     * 인증된 사용자가 존재하지 않거나 관리자 권한이 없으면 각각 UNAUTHORIZED 또는 FORBIDDEN 예외를 발생시킵니다.
     *
     * @return 관리자 권한을 가진 사용자의 ID
     * @throws AuthException 인증 정보가 없거나 권한이 부족할 때 발생
     */
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

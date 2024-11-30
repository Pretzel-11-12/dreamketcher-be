package pretzel.dreamketcherbe.auth.service.internal;


import org.springframework.stereotype.Component;;
import org.springframework.web.context.annotation.RequestScope;
import pretzel.dreamketcherbe.auth.exception.AuthException;
import pretzel.dreamketcherbe.auth.exception.AuthExceptionType;

@Component
@RequestScope
public class AuthContext {

    private Long memberId;

    public Long getMemberId() {
        if (this.memberId == null) {
            throw new AuthException(AuthExceptionType.UNAUTHORIZED);
        }
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}

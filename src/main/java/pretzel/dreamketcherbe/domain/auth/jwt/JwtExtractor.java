package pretzel.dreamketcherbe.domain.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pretzel.dreamketcherbe.domain.auth.exception.AuthException;
import pretzel.dreamketcherbe.domain.auth.exception.AuthExceptionType;
import pretzel.dreamketcherbe.domain.auth.repository.TokenExtractor;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtExtractor implements TokenExtractor {

    private static final String MEMBER_ID = "member_Id";
    private static final String TOKEN_ID = "token_id";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";

    private final JwtParser jwtParser;

    public JwtExtractor(@Value("${jwt.secret-key}") String secretKeyString){
        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parserBuilder()
                                .setSigningKey(secretKey)
                                .build();
    }

    @Override
    public Long extractAccessToken(String token){
        return extract(token, ACCESS_TOKEN, MEMBER_ID, Long.class);
    }

    @Override
    public String extractRefreshToken(String token){
        return extract(token, REFRESH_TOKEN, TOKEN_ID, String.class);
    }

    private <T> T extract(String token, String expectedTokenType, String claimKey, Class<T> T){
        Claims claims = parseClaim(token);
        String subject = claims.getSubject();

        if(subject.equals(expectedTokenType)){
            return claims.get(claimKey, T);
        }
        throw new AuthException(AuthExceptionType.INVALID_TOKEN_TYPE);
    }

    private Claims parseClaim(String token){
        try{
            return jwtParser.parseClaimsJws(token)
                            .getBody();
        }catch(Exception e){
            throw new AuthException(AuthExceptionType.INVALID_TOKEN);
        }
    }
}

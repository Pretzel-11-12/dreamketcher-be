package pretzel.dreamketcherbe.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pretzel.dreamketcherbe.auth.repository.TokenExtractor;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtExtractor implements TokenExtractor {

    private static final String MEMBER_ID = "memberId";

    private final JwtParser jwtParser;

    public JwtExtractor(@Value("${jwt.secret-key}") String secretKeyString){
        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parserBuilder()
                                .setSigningKey(secretKey)
                                .build();
    }

    @Override
    public Long extract(String token){
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        return claims.get(MEMBER_ID, Long.class);
    }
}

package pretzel.dreamketcherbe.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pretzel.dreamketcherbe.auth.dto.AuthPayload;

import io.jsonwebtoken.security.Keys;
import pretzel.dreamketcherbe.member.entity.Role;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider implements TokenProvider {

    private static final String MEMBER_ID = "memberId";
    private static final String ROLE = "role";

    private final SecretKey secretKey;
    private final Long EXPIRED;
    private final JwtParser jwtParser;

    public JwtProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.expiration}") Long expired
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.EXPIRED = expired;
        this.jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
    }

    @Override
    public String generated(AuthPayload authPayload){
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claim(MEMBER_ID, authPayload.memberId())
                .claim(ROLE, authPayload.role())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + EXPIRED))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public AuthPayload extract(String token){
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        Long memberId = claims.get(MEMBER_ID, Long.class);
        String role = claims.get(ROLE, String.class);
        return new AuthPayload(memberId, Role.of(role));
    }
}

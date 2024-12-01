package pretzel.dreamketcherbe.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pretzel.dreamketcherbe.auth.dto.AuthPayload;

import io.jsonwebtoken.security.Keys;
import pretzel.dreamketcherbe.member.entity.Role;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider implements TokenProvider {

    private static final String MEMBER_ID = "memberId";
    private static final String ROLE = "role";

    private final SecretKey secretKey;
    private final Long EXPIRED;

    public JwtProvider(
            @Value("${jwt.secret-key}") String secretKeyString,
            @Value("${jwt.expiration}") Long expired
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
        this.EXPIRED = expired;
    }

    @Override
    public String generated(AuthPayload authPayload){
        long now = System.currentTimeMillis();
        Claims claims = Jwts.claims();
        claims.put(MEMBER_ID, authPayload.memberId());
        claims.put(ROLE, authPayload.role().name());

        String jwtToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + EXPIRED))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        log.info("jwtToken: {}", jwtToken);
        return jwtToken;
    }
}

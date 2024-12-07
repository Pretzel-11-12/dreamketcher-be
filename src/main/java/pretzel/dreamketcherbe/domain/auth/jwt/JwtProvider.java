package pretzel.dreamketcherbe.domain.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;
import pretzel.dreamketcherbe.domain.auth.repository.TokenProvider;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider implements TokenProvider {

    private static final String MEMBER_ID = "member_id";
    private static final String TOKEN_ID = "token_id";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";

    private final SecretKey key;
    private final Long accessTokenExpired;
    private final Long refreshTokenExpired;

    public JwtProvider(
        @Value("${jwt.secret-key}") String secretKeyString,
        @Value("${jwt.access-exp}") Long accessTokenExpired,
        @Value("${jwt.refresh-exp}") Long refreshTokenExpired
    ) {
        this.key = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpired = accessTokenExpired;
        this.refreshTokenExpired = refreshTokenExpired;
    }

    @Override
    public String generatedAccessToken(Long memberId) {
        Claims claims = generatedClaims(MEMBER_ID, memberId);
        return generatedToken(claims, ACCESS_TOKEN, accessTokenExpired);
    }

    @Override
    public String generatedRefreshToken(String tokenId) {
        Claims claims = generatedClaims(TOKEN_ID, tokenId);
        return generatedToken(claims, REFRESH_TOKEN, refreshTokenExpired);
    }

    private Claims generatedClaims(String key, Object value) {
        Claims claims = Jwts.claims();
        claims.put(key, value);
        return claims;
    }

    private String generatedToken(Claims claims, String subject, Long expired) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(new Date(now))
                    .setExpiration(new Date(now + expired))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
    }
}
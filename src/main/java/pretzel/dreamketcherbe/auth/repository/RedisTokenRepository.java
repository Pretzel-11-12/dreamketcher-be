package pretzel.dreamketcherbe.auth.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import pretzel.dreamketcherbe.auth.entity.Token;
import pretzel.dreamketcherbe.common.exception.redis.RedisException;
import pretzel.dreamketcherbe.common.exception.redis.RedisExceptionType;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisTokenRepository implements TokenRepository {

    private static final Long TTL = 1000L * 60 * 60 * 24 * 7;
    private static final String PREFIX = "token";
    private static final String SEPARATOR = ":";
    private static final String DELIMITER = "";

    private final ObjectMapper ObjectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(Token token) {
        redisTemplate.opsForValue().set(key(token.getTokenId()), serializeToken(token));
        redisTemplate.expire(key(token.getTokenId()), TTL, TimeUnit.MINUTES);
    }

    @Override
    public void deleteByTokenId(String tokenId) {
        redisTemplate.delete(key(tokenId));
    }

    @Override
    public Optional<Token> findByTokenId(String tokenId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key(tokenId)))
            .map(this::deserializeToken);
    }

    private String key(String tokenId) {
        return String.join(DELIMITER, PREFIX, SEPARATOR, tokenId);
    }

    private String serializeToken(Token token) {
        try {
            return ObjectMapper.writeValueAsString(token);
        } catch (JsonProcessingException e) {
            throw new RedisException(RedisExceptionType.SERIALIZE_ERROR);
        }
    }

    private Token deserializeToken(String tokenJson) {
        try {
            return ObjectMapper.readValue(tokenJson, Token.class);
        } catch (JsonProcessingException e) {
            throw new RedisException(RedisExceptionType.DESERIALIZE_ERROR);
        }
    }
}

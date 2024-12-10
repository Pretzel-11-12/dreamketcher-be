package pretzel.dreamketcherbe.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record TokenResponse(
    String accessToken,
    @JsonIgnore String refreshToken
) {
    public static TokenResponse of(String accessToken, String refreshToken) {
        return new TokenResponse(accessToken, refreshToken);
    }
}

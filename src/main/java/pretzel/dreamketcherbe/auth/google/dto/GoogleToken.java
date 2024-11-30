package pretzel.dreamketcherbe.auth.google.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleToken(
    @JsonProperty("access_token") String accessToken
) {
}

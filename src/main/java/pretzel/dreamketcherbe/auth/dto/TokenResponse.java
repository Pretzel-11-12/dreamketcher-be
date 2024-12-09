package pretzel.dreamketcherbe.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record TokenResponse(
    String accessToken
) {
}

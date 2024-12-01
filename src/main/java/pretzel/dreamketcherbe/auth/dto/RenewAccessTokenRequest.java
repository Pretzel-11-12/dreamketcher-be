package pretzel.dreamketcherbe.auth.dto;

import jakarta.validation.constraints.NotNull;

public record RenewAccessTokenRequest(
    @NotNull String refreshToken
) {
}

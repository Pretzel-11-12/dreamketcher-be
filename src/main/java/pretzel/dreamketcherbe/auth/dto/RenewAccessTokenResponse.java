package pretzel.dreamketcherbe.auth.dto;

public record RenewAccessTokenResponse(
    String accessToken
) {

    public static RenewAccessTokenResponse of(String accessToken) {
        return new RenewAccessTokenResponse(accessToken);
    }
}

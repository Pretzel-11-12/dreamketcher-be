package pretzel.dreamketcherbe.domain.auth.google;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pretzel.dreamketcherbe.domain.auth.google.dto.GoogleToken;
import pretzel.dreamketcherbe.domain.auth.google.dto.GoogleUserInfo;

@Component
@RequiredArgsConstructor
public class GoogleOAuthClient {

    private static final String PROVIDER = "google";

    private final GoogleProperties googleProperties;
    private final GoogleApiClient googleApiClient;

    public GoogleUserInfo getOAuthInfo(String code, String redirectUri) {
        GoogleToken googleToken = googleApiClient.fetchToken(params(code, redirectUri));
        GoogleUserInfo googleUserInfo = googleApiClient.fetchUserInfo(
            "Bearer " + googleToken.accessToken());
        return googleUserInfo;
    }

    private MultiValueMap<String, String> params(String code, String redirectUri) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleProperties.clientId());
        params.add("client_secret", googleProperties.clientSecret());
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", googleProperties.grantType());
        return params;
    }
}

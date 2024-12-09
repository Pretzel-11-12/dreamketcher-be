package pretzel.dreamketcherbe.auth.google;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pretzel.dreamketcherbe.auth.google.dto.GoogleToken;
import pretzel.dreamketcherbe.auth.google.dto.GoogleUserInfo;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleOAuthClient {

    private static final String PROVIDER = "google";

    private final GoogleProperties googleProperties;
    private final GoogleApiClient googleApiClient;

    public GoogleUserInfo getOAuthInfo(String code) {
        GoogleToken googleToken = googleApiClient.fetchToken(params(code));
        GoogleUserInfo googleUserInfo = googleApiClient.fetchUserInfo("Bearer " + googleToken.accessToken());
        return googleUserInfo;
    }

    private MultiValueMap<String, String> params(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleProperties.clientId());
        params.add("client_secret", googleProperties.clientSecret());
        params.add("redirect_uri", googleProperties.redirectUri());
        params.add("grant_type", googleProperties.grantType());
        return params;
    }
}

package pretzel.dreamketcherbe.domain.auth.google;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import pretzel.dreamketcherbe.domain.auth.google.dto.GoogleToken;
import pretzel.dreamketcherbe.domain.auth.google.dto.GoogleUserInfo;

public interface GoogleApiClient {

    @PostExchange(url = "https://oauth2.googleapis.com/token")
    GoogleToken fetchToken(@RequestParam MultiValueMap<String, String> params);

    @GetExchange(url = "https://www.googleapis.com/oauth2/v3/userinfo")
    GoogleUserInfo fetchUserInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken);
}

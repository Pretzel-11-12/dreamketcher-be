package pretzel.dreamketcherbe.auth.jwt;


import pretzel.dreamketcherbe.auth.dto.AuthPayload;

public interface TokenProvider {

    String generated(AuthPayload authPayload);
}

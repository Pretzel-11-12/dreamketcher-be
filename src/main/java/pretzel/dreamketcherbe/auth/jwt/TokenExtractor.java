package pretzel.dreamketcherbe.auth.jwt;

import pretzel.dreamketcherbe.auth.dto.AuthPayload;

public interface TokenExtractor {

    AuthPayload extract(String token);
}

package pretzel.dreamketcherbe.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import pretzel.dreamketcherbe.domain.auth.google.GoogleApiClient;

@Configuration
public class HttpInterfaceConfig {

    @Bean
    public GoogleApiClient googleApiClient() {
        return createHttpInterface(GoogleApiClient.class);
    }

    private <T> T createHttpInterface(Class<T> proxyTpye) {
        WebClient webClient = WebClient.builder().build();
        return HttpServiceProxyFactory
            .builderFor(WebClientAdapter.create(webClient))
            .build()
            .createClient(proxyTpye);
    }
}

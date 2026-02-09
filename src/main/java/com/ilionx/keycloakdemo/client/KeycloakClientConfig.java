package com.ilionx.keycloakdemo.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class KeycloakClientConfig {

    @Value("${issuer}")
    private String issuer;

    @Bean
    public KeycloakClient keycloakCLient() {
        RestClient keycloakClient = RestClient.builder()
                .baseUrl(issuer)
                .build();
        RestClientAdapter adapter = RestClientAdapter.create(keycloakClient);
        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();
        return proxyFactory.createClient(KeycloakClient.class);
    }
}

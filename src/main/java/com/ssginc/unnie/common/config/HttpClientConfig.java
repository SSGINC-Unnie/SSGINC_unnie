package com.ssginc.unnie.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class HttpClientConfig {

    @Bean(name = "tossWebClient")
    public WebClient tossWebClient(WebClient.Builder builder) {
        return builder.baseUrl("https://api.tosspayments.com").build();
    }
}
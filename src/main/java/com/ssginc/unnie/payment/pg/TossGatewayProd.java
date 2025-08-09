package com.ssginc.unnie.payment.pg;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;


@Component
@ConditionalOnProperty(prefix = "pg", name = "mode", havingValue = "prod")
@RequiredArgsConstructor
public class TossGatewayProd implements TossGateway {

    @Value("${toss.secret-key}")
    private String secretKey;

    private WebClient webClient;

    @PostConstruct
    void init() {
        String basic = "Basic " + Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        this.webClient = WebClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, basic)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public void confirm(String paymentKey, String orderId, Long amount) {
        Map<String, Object> body = Map.of(
                "paymentKey", paymentKey,
                "orderId",    orderId,
                "amount",     amount
        );

        webClient.post()
                .uri("/v1/payments/confirm")
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class).flatMap(msg ->
                                Mono.error(new IllegalStateException(
                                        "TOSS confirm 실패: " + resp.statusCode() + " " + msg))))
                .toBodilessEntity()
                .block(); // 성공 시 그냥 끝, 실패 시 예외
    }
}
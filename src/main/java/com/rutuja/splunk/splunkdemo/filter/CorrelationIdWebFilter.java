package com.rutuja.splunk.splunkdemo.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Optional;
import java.util.UUID;

@Component
public class CorrelationIdWebFilter implements WebFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Optional<String> correlationIdOp = Optional.ofNullable(request.getHeaders().getFirst(CORRELATION_ID_HEADER));
        String correlationId= correlationIdOp.orElseGet(() -> UUID.randomUUID().toString());
        return chain.filter(exchange)
                .contextWrite(Context.of(CORRELATION_ID_HEADER, correlationId));
    }
}

package com.rutuja.splunk.splunkdemo.filter;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst("X-Correlation-ID");
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        // Correctly setting the correlation ID in the ThreadContext for each subscriber
        String finalCorrelationId = correlationId;
        MDC.put("correlationId", finalCorrelationId);
        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put("correlationId", finalCorrelationId))  // Adding to Reactor Context
                .doOnEach(signal -> {
                    if (!signal.isOnComplete()) {  // Check to avoid accessing context on completion
                        String cid = signal.getContextView().getOrDefault("correlationId", "default-correlation-id");
                        ThreadContext.put("correlationId", cid);
                    }
                })
                .doFinally(signalType -> ThreadContext.clearAll());  // Clearing ThreadContext after processing
    }
}

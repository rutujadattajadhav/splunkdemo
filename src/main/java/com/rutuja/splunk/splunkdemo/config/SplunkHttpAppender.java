package com.rutuja.splunk.splunkdemo.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.SslProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class SplunkHttpAppender extends AppenderBase<ILoggingEvent> {
    private String url;
    private String token;
    private WebClient webClient;

    public static WebClient createWebClient() {
        try {
            SslContext sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();

            HttpClient httpClient = HttpClient.create()
                    .secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

            return WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error creating SSL context", e);
        }
    }
    public void setUrl(String url) {
        this.url = url;
        this.webClient = createWebClient();
    }

    public void setToken(String token) {
        this.token = token;
    }



    @Override
    protected void append(ILoggingEvent eventObject) {
        String json = convertToJson(eventObject);
        webClient.post()
                .uri(url)
                .header("Authorization", "Splunk " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .retrieve()
                .bodyToMono(String.class)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error -> addError("Error sending log to Splunk", error))
                //.doOnSuccess(response -> System.out.println("Log sent to Splunk successfully"))
                .subscribe();
    }

    private String convertToJson(ILoggingEvent event) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> logMap = new HashMap<>();

        Map<String, Object> eventDetails = new HashMap<>();
        eventDetails.put("message", event.getFormattedMessage());
        eventDetails.put("service", "auth-service");  // Assuming you have a way to dynamically determine this
        eventDetails.put("level", event.getLevel().toString());
        eventDetails.put("timestamp", new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new java.util.Date(event.getTimeStamp())));
        eventDetails.put("correlation_id", MDC.get("correlationId"));  // Make sure correlationId is set in MDC where you log

        logMap.put("event", eventDetails);
        logMap.put("sourcetype", "_json");
        logMap.put("host", "localhost");  // This could also be dynamic if needed
        logMap.put("index", "api");  // This could also be dynamic if needed

        try {
            return mapper.writeValueAsString(logMap);
        } catch (IOException e) {
            return "{}";  // Return empty JSON on error
        }
    }
}

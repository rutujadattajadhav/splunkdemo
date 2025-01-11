package com.rutuja.splunk.splunkdemo.config;

import org.slf4j.MDC;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;

import java.util.function.Consumer;

public class ReactorMdcSetup {

    public static void setupContext() {
        Hooks.onEachOperator(ReactorMdcSetup.class.getName(), signal -> {
            if (signal.getType() != Signal.Type.ON_COMPLETE && signal.getType() != Signal.Type.ON_ERROR) {
                return signal.getContextView()
                        .<String>getOrEmpty(CorrelationIdWebFilter.CORRELATION_ID_HEADER)
                        .map(correlationId -> signal.map(context -> {
                            MDC.put(CorrelationIdWebFilter.CORRELATION_ID_HEADER, correlationId);
                            return context;
                        }))
                        .orElse(signal);
            }
            return signal;
        });
    }
}

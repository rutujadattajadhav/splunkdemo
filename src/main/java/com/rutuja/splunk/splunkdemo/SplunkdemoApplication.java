package com.rutuja.splunk.splunkdemo;

import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
public class SplunkdemoApplication {

	public static void main(String[] args) {
		MDC.put("correlationId", UUID.randomUUID().toString()+"_app_startup_log");
		SpringApplication.run(SplunkdemoApplication.class, args);
	}

}

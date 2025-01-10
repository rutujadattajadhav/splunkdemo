package com.rutuja.splunk.splunkdemo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class HttpEventCollectorSenderr {

    private static final String EVENT_COLLECTOR_URL = "http://localhost:8088/services/collector";

    public static void sendLog(String logMessage) throws IOException, InterruptedException {
        // Create a JSON object with the log message
        Map<String, String> logData = new HashMap<>();
        logData.put("data", logMessage);

        // Build the HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(EVENT_COLLECTOR_URL))
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtil.toJson(logData)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Splunk 92c9b613-cec1-47ad-a4d8-bc33c0bc7dc3")
                .build();

        // Send the request and handle the response
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check the response status code
        if (response.statusCode() != 200) {
            throw new RuntimeException("Error sending log: " + response.statusCode());
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String logMessage = "This is a test log message";
        sendLog(logMessage);
        System.out.println("Log message sent successfully.");
    }
}

// Utility class for JSON serialization (replace with your preferred library)
class JsonUtil {
    public static String toJson(Object object) {
        // Implement JSON serialization logic here
        return "";
    }
}

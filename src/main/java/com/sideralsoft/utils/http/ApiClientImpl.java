package com.sideralsoft.utils.http;

import com.sideralsoft.config.ApplicationProperties;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClientImpl<T> implements ApiClient<T> {
    @Override
    public HttpResponse<T> enviarPeticionGet(String url, HttpResponse.BodyHandler<T> bodyHandler) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + ApplicationProperties.getProperty("api.token"))
                .GET()
                .build();
        return client.send(request, bodyHandler);
    }

    @Override
    public HttpResponse<T> enviarPeticionPost(String url, HttpRequest.BodyPublisher bodyPublisher, HttpResponse.BodyHandler<T> bodyHandler) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + ApplicationProperties.getProperty("api.token"))
                .POST(bodyPublisher)
                .build();

        return client.send(request, bodyHandler);
    }
}

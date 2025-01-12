package com.sideralsoft.utils;

import com.sideralsoft.utils.http.ApiClient;

import javax.net.ssl.SSLSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MockApiClient<T> implements ApiClient<T> {

    private final Map<String, T> mockedResponses = new HashMap<>();

    public void addMockResponse(String request, T response) {
        mockedResponses.put(request, response);
    }

    @Override
    public HttpResponse<T> enviarPeticionGet(String url, HttpResponse.BodyHandler<T> bodyHandler) throws Exception {
        if (!mockedResponses.containsKey(url)) {
            throw new RuntimeException("URL no mapeada en el mock: " + url);
        }
        return new HttpResponse<T>() {
            @Override
            public int statusCode() {
                return 200; // Simula éxito
            }

            @Override
            public HttpRequest request() {
                return null;
            }

            @Override
            public Optional<HttpResponse<T>> previousResponse() {
                return Optional.empty();
            }

            @Override
            public HttpHeaders headers() {
                return null;
            }

            @Override
            public T body() {
                return mockedResponses.get(url);
            }

            @Override
            public Optional<SSLSession> sslSession() {
                return Optional.empty();
            }

            @Override
            public URI uri() {
                return null;
            }

            @Override
            public HttpClient.Version version() {
                return null;
            }
        };
    }

    @Override
    public HttpResponse<T> enviarPeticionPost(String url, HttpRequest.BodyPublisher bodyPublisher, HttpResponse.BodyHandler<T> bodyHandler) throws Exception {
        return null;
    }
}

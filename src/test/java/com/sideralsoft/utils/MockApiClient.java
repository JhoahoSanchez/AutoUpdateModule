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

public class MockApiClient implements ApiClient<String> {

    private final Map<String, String> mockedResponses = new HashMap<>();

    public void addMockResponse(String request, String response) {
        mockedResponses.put(request, response);
    }

    @Override
    public HttpResponse<String> enviarPeticionGet(String url, HttpResponse.BodyHandler<String> bodyHandler) throws Exception {
        if (!mockedResponses.containsKey(url)) {
            throw new RuntimeException("URL no mapeada en el mock: " + url);
        }

        return new HttpResponse<String>() {
            @Override
            public int statusCode() {
                return 200;
            }

            @Override
            public HttpRequest request() {
                return null;
            }

            @Override
            public Optional<HttpResponse<String>> previousResponse() {
                return Optional.empty();
            }

            @Override
            public HttpHeaders headers() {
                return null;
            }

            @Override
            public String body() {
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
    public HttpResponse<String> enviarPeticionPost(String url, HttpRequest.BodyPublisher bodyPublisher, HttpResponse.BodyHandler<String> bodyHandler) throws Exception {
        if (!mockedResponses.containsKey(url)) {
            throw new RuntimeException("URL no mapeada en el mock: " + url);
        }
        return new HttpResponse<String>() {
            @Override
            public int statusCode() {
                return 200;
            }

            @Override
            public HttpRequest request() {
                return null;
            }

            @Override
            public Optional<HttpResponse<String>> previousResponse() {
                return Optional.empty();
            }

            @Override
            public HttpHeaders headers() {
                return null;
            }

            @Override
            public String body() {
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
}

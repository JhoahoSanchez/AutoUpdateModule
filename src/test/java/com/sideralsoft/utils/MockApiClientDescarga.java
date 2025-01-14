package com.sideralsoft.utils;

import com.sideralsoft.utils.http.ApiClient;

import javax.net.ssl.SSLSession;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MockApiClientDescarga implements ApiClient<InputStream> {
    private final Map<String, byte[]> mockedResponses = new HashMap<>();

    public void addMockResponse(String request, byte[] response) {
        mockedResponses.put(request, response);
    }

    @Override
    public HttpResponse<InputStream> enviarPeticionGet(String url, HttpResponse.BodyHandler<InputStream> bodyHandler) throws Exception {
        if (!mockedResponses.containsKey(url)) {
            throw new RuntimeException("URL no mapeada en el mock: " + url);
        }

        byte[] responseContent = mockedResponses.get(url);
        InputStream stream = new ByteArrayInputStream(responseContent);

        return new HttpResponse<>() {
            @Override
            public int statusCode() {
                return 200;
            }

            @Override
            public HttpRequest request() {
                return null;
            }

            @Override
            public Optional<HttpResponse<InputStream>> previousResponse() {
                return Optional.empty();
            }

            @Override
            public HttpHeaders headers() {
                Map<String, List<String>> headersMap = Map.of(
                        "Content-Disposition", List.of("attachment; filename=\"archivo-prueba.zip\""),
                        "Content-Type", List.of("application/octet-stream")
                );

                return HttpHeaders.of(headersMap, (key, value) -> true);
            }

            @Override
            public InputStream body() {
                return stream;
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
    public HttpResponse<InputStream> enviarPeticionPost(String url, HttpRequest.BodyPublisher bodyPublisher, HttpResponse.BodyHandler<InputStream> bodyHandler) throws Exception {
        if (!mockedResponses.containsKey(url)) {
            throw new RuntimeException("URL no mapeada en el mock: " + url);
        }

        byte[] responseContent = mockedResponses.get(url);
        InputStream stream = new ByteArrayInputStream(responseContent);

        return new HttpResponse<InputStream>() {
            @Override
            public int statusCode() {
                return 200;
            }

            @Override
            public HttpRequest request() {
                return null;
            }

            @Override
            public Optional<HttpResponse<InputStream>> previousResponse() {
                return Optional.empty();
            }

            @Override
            public HttpHeaders headers() {
                return null;
            }

            @Override
            public InputStream body() {
                return stream;
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

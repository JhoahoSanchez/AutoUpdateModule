package com.sideralsoft.utils.http;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface ApiClient<T> {

    HttpResponse<T> enviarPeticionGet(String url, HttpResponse.BodyHandler<T> bodyHandler) throws Exception;

    HttpResponse<T> enviarPeticionPost(String url, HttpRequest.BodyPublisher bodyPublisher, HttpResponse.BodyHandler<T> bodyHandler) throws Exception;
}

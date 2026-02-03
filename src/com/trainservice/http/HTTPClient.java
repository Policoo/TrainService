package com.trainservice.http;

public interface HTTPClient {
    Response post(String url, Object body);

    Response get(String url);
}

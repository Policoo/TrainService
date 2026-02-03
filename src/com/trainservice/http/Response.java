package com.trainservice.http;

public interface Response {
    int getStatusCode();

    Object getBody();
}

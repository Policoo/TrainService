package com.trainservice.router;

import com.trainservice.http.Response;
import com.trainservice.http.SimpleResponse;
import java.util.Map;

public abstract class Controller {

    public Response handle(String method, Map<String, String> parameters, Object body) {
        switch (method.toUpperCase()) {
            case "GET":
                return doGet(parameters, body);
            case "POST":
                return doPost(parameters, body);
            default:
                return new SimpleResponse(405, "Method Not Allowed");
        }
    }

    public Response doGet(Map<String, String> parameters, Object body) {
        return new SimpleResponse(405, "Method Not Allowed");
    }

    public Response doPost(Map<String, String> parameters, Object body) {
        return new SimpleResponse(405, "Method Not Allowed");
    }
}

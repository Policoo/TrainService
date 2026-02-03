package com.trainservice.http;

import java.util.Map;

public class ErrorResponse implements Response {
    private final int statusCode;
    private final Map<String, String> body;

    public ErrorResponse(int statusCode, String error, String message) {
        this.statusCode = statusCode;
        this.body = Map.of("error", error, "message", message);
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public Object getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HTTP " + statusCode + "\n" + prettyString(body.toString());
    }

    private String prettyString(String input) {
        StringBuilder sb = new StringBuilder();
        int indentLevel = 0;
        String indent = "  ";

        for (char c : input.toCharArray()) {
            switch (c) {
                case '{':
                case '[':
                    indentLevel++;
                    sb.append(c).append("\n").append(indent.repeat(indentLevel));
                    break;
                case '}':
                case ']':
                    indentLevel--;
                    if (indentLevel < 0) indentLevel = 0;
                    sb.append("\n").append(indent.repeat(indentLevel)).append(c);
                    break;
                case ',':
                    sb.append(c).append("\n").append(indent.repeat(indentLevel));
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
}

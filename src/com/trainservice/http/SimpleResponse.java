package com.trainservice.http;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class SimpleResponse implements Response {
    private final int statusCode;
    private final Object body;

    public SimpleResponse(int statusCode, Object body) {
        this.statusCode = statusCode;
        this.body = body;
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
        return "HTTP " + statusCode + "\n" + prettyString(Objects.toString(body));
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

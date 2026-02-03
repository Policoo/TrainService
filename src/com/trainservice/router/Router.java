package com.trainservice.router;

import com.trainservice.http.Response;
import com.trainservice.http.SimpleResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Router {
    private final List<RouteMapping> routes = new ArrayList<>();

    public void register(String template, Controller controller) {
        routes.add(new RouteMapping(template, controller));
    }

    public Response dispatch(String method, String url, Object body) {
        for (RouteMapping route : routes) {
            Map<String, String> params = extractParameters(route.getTemplate(), url);
            if (params != null) {
                return route.getController().handle(method, params, body);
            }
        }

        return new SimpleResponse(404, "Not Found: " + method + " " + url);
    }

    private Map<String, String> extractParameters(String template, String url) {
        String regex = template.replaceAll("\\{([^/]+)\\}", "(?<$1>[^/]+)");
        Pattern pattern = Pattern.compile("^" + regex + "$");
        Matcher matcher = pattern.matcher(url);

        if (matcher.matches()) {
            Map<String, String> params = new HashMap<>();
            Matcher groupMatcher = Pattern.compile("\\{([^/]+)\\}").matcher(template);

            while (groupMatcher.find()) {
                String groupName = groupMatcher.group(1);
                params.put(groupName, matcher.group(groupName));
            }

            return params;
        }

        return null;
    }
}

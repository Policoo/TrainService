package com.trainservice.router;

public class RouteMapping {
    private final String template;
    private final Controller controller;

    public RouteMapping(String template, Controller controller) {
        this.template = template;
        this.controller = controller;
    }

    public String getTemplate() {
        return template;
    }

    public Controller getController() {
        return controller;
    }
}

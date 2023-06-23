package net.flares.flaretokens.service;

import lombok.Getter;

@Getter
public enum ServiceHandler {
    SERVICE;

    private MenuService menuService = new MenuService();
    private LoggerService loggerService = new LoggerService();
    private DataService dataService = new DataService();

}

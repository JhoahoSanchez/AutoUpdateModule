package com.sideralsoft.config;

import spark.Spark;

public class AppConfig {

    private static final int DEFAULT_PORT = 5500;
    private static AppConfig instance;

    private AppConfig() {
        configureSpark();
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public int getPort() {
        return DEFAULT_PORT;
    }

    private void configureSpark(){
        Spark.port(getPort());
        Spark.threadPool(8); //TODO: COMPROBAR
    }

}

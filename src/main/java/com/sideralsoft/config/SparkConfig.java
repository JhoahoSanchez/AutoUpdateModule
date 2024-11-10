package com.sideralsoft.config;

import spark.Spark;

public class SparkConfig {

    private static SparkConfig instance;

    private SparkConfig() {
        configureSpark();
    }

    public static SparkConfig getInstance() {
        if (instance == null) {
            instance = new SparkConfig();
        }
        return instance;
    }

    private void configureSpark() {
        int port = Integer.parseInt(ApplicationProperties.getInstance().getProperty("spark.defaultPort"));
        Spark.port(port);
        int maxThreads = Integer.parseInt(ApplicationProperties.getInstance().getProperty("spark.maxThreads"));
        Spark.threadPool(maxThreads); //TODO: COMPROBAR
    }

}

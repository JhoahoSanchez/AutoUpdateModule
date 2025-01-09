package com.sideralsoft;

import com.sideralsoft.config.SparkConfig;
import com.sideralsoft.service.SchedulerService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {

            executor.submit(() -> {
                SchedulerService schedulerService = new SchedulerService();
                schedulerService.generarProcesoActualizacion();
            });

            executor.submit(() -> {
                SparkConfig.getInstance();
            });

            try {
                System.out.println("Press Ctrl+C to stop the application.");
                Thread.currentThread().join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            executor.shutdown();
            System.out.println("Application finished.");
        }
    }
}
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

            executor.shutdown();
        }

        /*
        TODO:
        - VER LA MANERA DE AGREGAR LOS PROCESOS A EL APPCONFIG /BASE DE DATOS
        - REALIZAR PRUEBAS DE TODOS LOS COMPONENTES //4/6


         */
    }
}
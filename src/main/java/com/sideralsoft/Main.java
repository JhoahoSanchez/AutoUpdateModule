package com.sideralsoft;

import com.sideralsoft.config.SparkConfig;

public class Main {
    public static void main(String[] args) {
//        SchedulerService schedulerService = new SchedulerService();
//        schedulerService.generarProcesoActualizacion();

        SparkConfig.getInstance();

        /*
        TODO:
        - IMPLEMENTAR ROLLBACK (COPIAR LOGICA DE INSTALACION DE ROLLBACK) EXCLUSIVO ACTUALIZACION
        - VER LA MANERA DE AGREGAR LOS PROCESOS A EL APPCONFIG
        - IMPLEMENTAR ACTUALIZACION PROPIA
        - REALIZAR PRUEBAS DE TODOS LOS COMPONENTES

         */
    }
}
package com.sideralsoft;

import com.sideralsoft.config.SparkConfig;

public class Main {
    public static void main(String[] args) {
//        SchedulerService schedulerService = new SchedulerService();
//        schedulerService.generarProcesoActualizacion();

        SparkConfig.getInstance();

        /*
        TODO:
        - IMPLEMENTAR ACTUALIZACION PROPIA
        - VER LA MANERA DE AGREGAR LOS PROCESOS A EL APPCONFIG
        - REALIZAR PRUEBAS DE TODOS LOS COMPONENTES

        -- agregar excepciones en actualizaciones para generar tarea de actualizacion luego X tiempo
         */
    }
}
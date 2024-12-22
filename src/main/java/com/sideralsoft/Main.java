package com.sideralsoft;

import com.sideralsoft.service.SchedulerService;

public class Main {
    public static void main(String[] args) {
        SchedulerService schedulerService = new SchedulerService();
        schedulerService.generarProcesoActualizacion();
    }
}
package com.sideralsoft;

import com.sideralsoft.service.SchedulerService;

public class Main {
    public static void main(String[] args) {
//        DetalleCertificados d = new DetalleCertificados();
//        d.comprobarValidezCertificados();
        SchedulerService schedulerService = new SchedulerService();
        schedulerService.actualizarElementos();

    }
}
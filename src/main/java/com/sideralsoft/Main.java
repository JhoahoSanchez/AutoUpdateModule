package com.sideralsoft;

import com.sideralsoft.config.Scheduler;

public class Main {
    public static void main(String[] args) {
//        DetalleCertificados d = new DetalleCertificados();
//        d.comprobarValidezCertificados();
        Scheduler scheduler = new Scheduler();
        scheduler.generarTareaConsulta();

    }
}
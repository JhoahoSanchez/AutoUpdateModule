package com.sideralsoft;

import com.sideralsoft.Config.Scheduler;

import static spark.Spark.get;

public class Main {
    public static void main(String[] args) {
        //get("/helloworld", (req, res) -> "Hello World");
//        DetalleCertificados d = new DetalleCertificados();
//        d.comprobarValidezCertificados();
        Scheduler scheduler = new Scheduler();
        scheduler.generarTareaConsulta();

    }
}
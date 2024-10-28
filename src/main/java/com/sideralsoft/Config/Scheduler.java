package com.sideralsoft.Config;

import com.sideralsoft.Service.ConsultaService;

public class Scheduler {

    private static Long horaConsulta = 3600L;

    public Scheduler(){
        AppConfig.getInstance();
    }

    public void generarTareaConsulta() {
        ConsultaService consultaService = new ConsultaService();
        consultaService.consultarNuevaVersion();
    }

    private void generarNuevaHoraConsulta() {
        Scheduler.horaConsulta = Math.round(Math.random() * horaConsulta); //TODO: ARREGLAR PARA QUE ESTE DENTRO DE LAS 24 HORAS
    }

}

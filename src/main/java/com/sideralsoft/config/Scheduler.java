package com.sideralsoft.config;

import com.sideralsoft.service.ActualizacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {

    private static final Logger LOG = LoggerFactory.getLogger(Scheduler.class);

    private final ActualizacionService actualizacionService;
    private final ScheduledExecutorService scheduler;

    public Scheduler(){
        AppConfig.getInstance();
        this.actualizacionService = new ActualizacionService();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void generarTareaConsulta() {
        try{
            this.actualizacionService.consultarNuevaVersion();
        }catch (Exception e){
            LOG.error("Error general al generar la tarea de consulta: ", e);
        }finally {
            this.generarNuevaHoraConsulta();
        }
    }

    private void generarNuevaHoraConsulta() {
        LocalDateTime horaEspecifica = LocalDateTime.now().plusHours((int) ((Math.random() * (10 - 2)) + 2));
        long delay = LocalDateTime.now().until(horaEspecifica, ChronoUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::generarTareaConsulta, delay, TimeUnit.HOURS.toSeconds(24), TimeUnit.SECONDS);
    }

    public void detenerScheduler() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

}

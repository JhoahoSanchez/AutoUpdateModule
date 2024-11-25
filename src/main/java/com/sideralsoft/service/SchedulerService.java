package com.sideralsoft.service;

import com.sideralsoft.config.SparkConfig;
import com.sideralsoft.domain.model.Elemento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulerService {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerService.class);

    private final ActualizacionService actualizacionService;
    private final ScheduledExecutorService scheduler;

    public SchedulerService() {
        SparkConfig.getInstance();
        this.actualizacionService = new ActualizacionService();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void generarProcesoActualizacion() {
        try {
            actualizacionService.actualizarElementos();
        } catch (Exception e) {
            LOG.error("Error general al generar la tarea de consulta: ", e);
        } finally {
            this.generarNuevaHoraConsulta();
        }
    }

    private void generarNuevaHoraConsulta() {
        LocalDateTime horaEspecifica = LocalDateTime.now().plusHours((int) ((Math.random() * (10 - 2)) + 2));
        long delay = LocalDateTime.now().until(horaEspecifica, ChronoUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::generarProcesoActualizacion, delay, TimeUnit.HOURS.toSeconds(24), TimeUnit.SECONDS);
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

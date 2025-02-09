package com.sideralsoft.service;

import com.sideralsoft.domain.Aplicacion;
import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.domain.model.TipoElemento;
import com.sideralsoft.utils.ElementosSingleton;
import com.sideralsoft.utils.http.ApiClientImpl;
import com.sideralsoft.utils.http.InstruccionResponse;
import org.apache.commons.lang3.StringUtils;
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
    private final ConsultaService consultaService;
    private final ScheduledExecutorService scheduler;

    public SchedulerService() {
        this.actualizacionService = new ActualizacionService(new DescargaService(new ApiClientImpl<>()));
        this.consultaService = new ConsultaService(new ApiClientImpl<>());
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void generarProcesoActualizacion() {
        try {
            LOG.debug("Iniciando proceso de actualizacion.");
            List<Elemento> elementos = ElementosSingleton.getInstance().obtenerElementos();
            for (Elemento elemento : elementos) {
                String version = consultaService.existeActualizacionDisponible(elemento.getNombre(), elemento.getVersion(), elemento.getTipo());

                if (StringUtils.isBlank(version)) {
                    if (elemento.getTipo().equals(TipoElemento.APLICACION) && elemento.getDependencias() != null) {
                        Aplicacion aplicacion = new Aplicacion(elemento);
                        elemento.setDependencias(aplicacion.actualizarDependencias());
                    }
                    continue;
                }

                List<InstruccionResponse> instrucciones = null;

                if (elemento.getTipo().equals(TipoElemento.APLICACION) || elemento.getTipo().equals(TipoElemento.APLICACION_GESTOR)) {
                    instrucciones = consultaService.obtenerInstrucciones(elemento, version);

                    if (instrucciones == null || instrucciones.isEmpty()) {
                        LOG.debug("No se ha encontrado instrucciones de actualizacion para " + elemento.getNombre());
                        continue;
                    }
                }

                if (actualizacionService.actualizarElemento(elemento, instrucciones, version)) {
                    elemento.setVersion(version);
                }
            }
            ElementosSingleton.getInstance().actualizarArchivoElementos(elementos);
        } catch (Exception e) {
            LOG.error("Error general al intentar actualizar los elementos: ", e);
        } finally {
            this.generarNuevaHoraConsulta();
        }
    }

    private void generarNuevaHoraConsulta() {
        LocalDateTime horaEspecifica = LocalDateTime.now().plusHours((int) ((Math.random() * (8)) + 2));
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

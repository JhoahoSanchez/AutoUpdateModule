package com.sideralsoft.domain;

import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.utils.JsonUtils;
import com.sideralsoft.utils.exception.ActualizacionException;
import com.sideralsoft.utils.http.InstruccionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class GestorActualizaciones extends Aplicacion {

    private static final Logger LOG = LoggerFactory.getLogger(GestorActualizaciones.class);

    public GestorActualizaciones(Elemento elemento, String ruta, List<InstruccionResponse> instrucciones) {
        super(elemento, ruta, instrucciones);
    }

    @Override
    public void actualizar() throws ActualizacionException {
        try {
            Path archivoInstrucciones = Path.of(this.rutaTemporal, "instrucciones.json");
            JsonUtils.toJsonFile(instrucciones, archivoInstrucciones.toFile());
            new ProcessBuilder("java", "-jar", "updater.jar").start();
            System.exit(0);
        } catch (IOException e) {
            LOG.error("Ha ocurrido un error general al intentar actualizar el elemento " + elemento.getNombre(), e);
            throw new ActualizacionException("Ha ocurrido un error general al intentar actualizar el elemento " + elemento.getNombre(), e);
        }
    }
}

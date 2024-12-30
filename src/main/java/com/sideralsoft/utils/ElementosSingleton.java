package com.sideralsoft.utils;

import com.sideralsoft.config.ApplicationProperties;
import com.sideralsoft.domain.model.Elemento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ElementosSingleton {

    private static ElementosSingleton instance;
    private static final Logger LOG = LoggerFactory.getLogger(ElementosSingleton.class);
    private static final String rutaArchivo = ApplicationProperties.getProperty("app.config.elementos.rutaArchivo");

    private ElementosSingleton() {
    }

    public static ElementosSingleton getInstance() {
        if (instance == null) {
            instance = new ElementosSingleton();
        }
        return instance;
    }

    public List<Elemento> obtenerElementos() {
        List<Elemento> elementos = null;
        try {
            elementos = JsonUtils.fromJsonFileToList(new File(rutaArchivo), Elemento.class);
            LOG.debug("Elementos encontrados: {}", elementos.toString());
            return elementos;
        } catch (Exception e) {
            LOG.error("Ha ocurrido un error durante la lectura del archivo de elementos: ", e);
        }
        return elementos;
    }

    public void actualizarArchivoElementos(List<Elemento> elementos) {
        try {
            JsonUtils.getMapper().writerWithDefaultPrettyPrinter().writeValue(new File(rutaArchivo), elementos);
        } catch (IOException e) {
            LOG.error("Ha ocurrido un error durante la actualizacion del archivo de elementos: ", e);
        }
    }

    public Elemento obtenerElemento(String nombre) {
        return this
                .obtenerElementos()
                .stream()
                .filter(elemento -> elemento.getNombre().equals(nombre))
                .findFirst()
                .orElse(null);
    }
}

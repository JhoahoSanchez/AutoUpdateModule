package com.sideralsoft.utils;

import com.sideralsoft.config.ApplicationProperties;
import com.sideralsoft.domain.model.Elemento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class ElementosSingleton {

    private static ElementosSingleton instance;
    private static final Logger LOG = LoggerFactory.getLogger(ElementosSingleton.class);
    private static final String rutaArchivo = ApplicationProperties.getInstance().getProperty("app.config.elementos.rutaArchivo");

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


}

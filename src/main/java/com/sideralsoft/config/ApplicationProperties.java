package com.sideralsoft.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {

    private static ApplicationProperties instance;
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationProperties.class);

    private Properties properties;

    private ApplicationProperties() {
        cargarPropiedades();
    }

    public static ApplicationProperties getInstance() {
        if (instance == null) {
            instance = new ApplicationProperties();
        }
        return instance;
    }

    private void cargarPropiedades() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                LOG.error("No se pudo encontrar el archivo application.properties");
                return;
            }
            if (properties != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            LOG.error("Error al leer el archivo application.properties: ", e);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

}

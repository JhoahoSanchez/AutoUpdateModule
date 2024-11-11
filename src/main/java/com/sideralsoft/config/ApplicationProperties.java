package com.sideralsoft.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationProperties.class);

    private static Properties properties;

    private static void cargarPropiedades() {
        properties = new Properties();
        try (InputStream input = ApplicationProperties.class.getClassLoader().getResourceAsStream("application.properties")) {
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

    public static String getProperty(String key) {
        if (properties == null) {
            ApplicationProperties.cargarPropiedades();
        }
        return properties.getProperty(key);
    }

}

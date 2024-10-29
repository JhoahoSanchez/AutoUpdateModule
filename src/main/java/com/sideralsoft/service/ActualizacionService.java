package com.sideralsoft.service;

import com.sideralsoft.config.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

public class ActualizacionService {

    //archivo de configuraciones
    private static final Logger LOG = LoggerFactory.getLogger(ActualizacionService.class);

    public void consultarNuevaVersion() {

        //METODO ENCARGADO DE ENVIAR LA INFO DE LAS APPS LOCALES A LA API Y RECIBIR UN JSON CON LA INFO DE LAS APPS DISPONIBLES A ACTUALIZAR
        //EN CASO DE QUE EL JSON ESTE VACIO, SE LANZA UNA EXCEPCION

        LOG.debug("Consulter Nueva Version");

//        Spark.get("/prueba", ((request, response) -> {
//            response.type("application/json");
//            logger.info("Dentro de la peticion get");
//            return "{\"message\": \"Hello, Spark!\"}";
//        }));

    }

    private void leerArchivoConfiguraciones(){

    }

}

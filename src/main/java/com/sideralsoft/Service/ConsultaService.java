package com.sideralsoft.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

public class ConsultaService {

    public void consultarNuevaVersion(){

        Logger logger = LoggerFactory.getLogger(ConsultaService.class);

        logger.debug("Consultar Nueva Version");

        Spark.get("/prueba", ((request, response) -> {
            response.type("application/json");
            logger.info("Dentro de la peticion get");
            return "{\"message\": \"Hello, Spark!\"}";
        }));

    }

}

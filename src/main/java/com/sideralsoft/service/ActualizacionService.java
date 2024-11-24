package com.sideralsoft.service;

import com.sideralsoft.config.ApplicationProperties;
import com.sideralsoft.utils.ElementosSingleton;
import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ActualizacionService {

    private static final Logger LOG = LoggerFactory.getLogger(ActualizacionService.class);

    public List<Elemento> consultarNuevaVersion() {

//        LOG.debug("Consulter Nueva Version");
//        List<Elemento> elementos = ElementosSingleton.getInstance().obtenerElementos();
//
//        try (HttpClient client = HttpClient.newHttpClient()) {
//
//            String json = JsonUtils.getMapper().writeValueAsString(elementos);
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(new URI(ApplicationProperties.getProperty("api.url")))
//                    .header("Content-Type", "application/json")
//                    .header("Authorization", ApplicationProperties.getProperty("api.token"))
//                    .POST(HttpRequest.BodyPublishers.ofString(json))
//                    .build();
//
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); //TODO: SE DESCARGA DIRECTAMENTE, VERIFICAR
//
//            if (response.statusCode() == 200) {
//                LOG.debug("Nuevas versiones encontradas");
//                List<Elemento> elementosActualizables = JsonUtils.fromJsonToList(response.body(), Elemento.class);
//
//                for (Elemento elemento : elementosActualizables) {
//                    LOG.debug("Elemento a actualizar: {}", elemento);
//                }
//                return elementosActualizables;
//            }
//        } catch (Exception e) {
//            LOG.error("Ha ocurrido un error al consultar el elemento: ", e);
//        }
        return null;
    }

    public void descargarArchivos(List<Elemento> elementosActualizables) {

    }
}

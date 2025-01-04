package com.sideralsoft.service;

import com.sideralsoft.config.ApplicationProperties;
import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.utils.JsonUtils;
import com.sideralsoft.utils.exception.ActualizacionException;
import com.sideralsoft.utils.exception.InstalacionException;
import com.sideralsoft.utils.http.ActualizacionResponse;
import com.sideralsoft.utils.http.InstruccionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ConsultaService {

    private static final Logger LOG = LoggerFactory.getLogger(ConsultaService.class);

    public String existeActualizacionDisponible(Elemento elemento) throws ActualizacionException {
        String nombre = URLEncoder.encode(elemento.getNombre(), StandardCharsets.UTF_8);
        String version = URLEncoder.encode(elemento.getVersion(), StandardCharsets.UTF_8);
        String baseUrl = ApplicationProperties.getProperty("api.url") + "/buscar-actualizacion";
        String urlConParametros = String.format("%s?nombre=%s&version=%s", baseUrl, nombre, version);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlConParametros))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + ApplicationProperties.getProperty("api.token"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 204) {
                LOG.debug("No existen nuevas versiones disponibles");
                return null;
            }

            if (response.statusCode() == 200) {
                ActualizacionResponse actualizacionResponse = JsonUtils.fromJson(response.body(), ActualizacionResponse.class);
                LOG.debug("Existe una nueva versión del elemento {} a la version {}", elemento.getNombre(), actualizacionResponse.getVersion());
                return actualizacionResponse.getVersion();
            }
        } catch (Exception e) {
            LOG.error("Error al consultar la API:", e);
            throw new ActualizacionException("Error al consultar la API", e);
        }
        return null;
    }

    public String existeInstalacionDisponible(String nombre) throws InstalacionException {
        String nombreTratado = URLEncoder.encode(nombre, StandardCharsets.UTF_8);
        String baseUrl = ApplicationProperties.getProperty("api.url") + "/buscar-recurso";
        String urlConParametros = String.format("%s?nombre=%s", baseUrl, nombreTratado);

        LOG.debug(urlConParametros);

        try {
            HttpClient client = HttpClient.newHttpClient(); //TODO: AGREGAR EL TOKEN
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlConParametros))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + ApplicationProperties.getProperty("api.token"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                LOG.debug("Ha ocurrido un error al intentar consultar la API: " + response.body());
                return null;
            }

            ActualizacionResponse actualizacionResponse = JsonUtils.fromJson(response.body(), ActualizacionResponse.class);
            LOG.debug("Se ha encontrado el elemento {} con la version {}", nombreTratado, actualizacionResponse.getVersion());
            return actualizacionResponse.getVersion();
        } catch (Exception e) {
            LOG.error("Error al consultar la API.", e);
            throw new InstalacionException("Error al consultar la API", e);
        }
    }

    public List<InstruccionResponse> obtenerInstrucciones(Elemento elemento, String version) throws ActualizacionException {
        String nombre = URLEncoder.encode(elemento.getNombre(), StandardCharsets.UTF_8);
        String versionActual = URLEncoder.encode(elemento.getVersion(), StandardCharsets.UTF_8);
        String versionActualizable = URLEncoder.encode(version, StandardCharsets.UTF_8);
        String baseUrl = ApplicationProperties.getProperty("api.url") + "/obtener-instrucciones";
        String urlConParametros = String.format("%s?nombre=%s&versionActual=%s&versionActualizable=%s", baseUrl, nombre, versionActual, versionActualizable);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlConParametros))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + ApplicationProperties.getProperty("api.token"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return JsonUtils.fromJsonToList(response.body(), InstruccionResponse.class);
            }
        } catch (Exception e) {
            LOG.error("Error al consultar instrucciones", e);
            throw new ActualizacionException("Error al consultar instrucciones", e);
        }
        return null;
    }
}

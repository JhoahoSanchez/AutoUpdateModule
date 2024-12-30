package com.sideralsoft.service;

import com.sideralsoft.config.ApplicationProperties;
import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.utils.JsonUtils;
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

    public String existeActualizacionDisponible(Elemento elemento) {
        String nombre = URLEncoder.encode(elemento.getNombre(), StandardCharsets.UTF_8);
        String version = URLEncoder.encode(elemento.getVersion(), StandardCharsets.UTF_8);
        String baseUrl = ApplicationProperties.getProperty("api.url") + "/buscar-actualizacion";
        String urlConParametros = String.format("%s?nombre=%s&version=%s", baseUrl, nombre, version);

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlConParametros))
                    .header("Content-Type", "application/json")
                    .header("Authorization", ApplicationProperties.getProperty("api.token"))
                    .GET()
                    .build();

            // Enviar la solicitud y obtener la respuesta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Interpretar la respuesta
            if (response.statusCode() == 204) {
                LOG.debug("No existen nuevas versiones disponibles");
                return null;
            }

            if (response.statusCode() == 200) {
                // Parsear la respuesta JSON (si el contenido está presente)
                ActualizacionResponse actualizacionResponse = JsonUtils.fromJson(response.body(), ActualizacionResponse.class);
                LOG.debug("Existe una nueva versión del elemento {} a la version {}", elemento.getNombre(), actualizacionResponse.getVersion());
                return actualizacionResponse.getVersion();
            } else {
                LOG.debug("Error al consultar la API: {}", response.body());
            }
        } catch (Exception e) {
            LOG.error("Error al consultar la API: {}", e.getMessage());
        }
        return null;
    }

    public String existeInstalacionDisponible(String nombre) {
        String nombreTratado = URLEncoder.encode(nombre, StandardCharsets.UTF_8);
        String baseUrl = ApplicationProperties.getProperty("api.url") + "/buscar-recurso";
        String urlConParametros = String.format("%s?nombre=%s", baseUrl, nombreTratado);

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlConParametros))
                    .header("Content-Type", "application/json")
                    .header("Authorization", ApplicationProperties.getProperty("api.token"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                LOG.debug("No se ha encontrado el elemento.");
                return null;
            }

            ActualizacionResponse actualizacionResponse = JsonUtils.fromJson(response.body(), ActualizacionResponse.class);
            LOG.debug("Se ha encontrado el elemento {} con la version {}", nombreTratado, actualizacionResponse.getVersion());
            return actualizacionResponse.getVersion();
        } catch (Exception e) {
            LOG.error("Error al consultar la API: {}", e.getMessage());
            return null;
        }
    }

    public List<InstruccionResponse> obtenerInstrucciones(Elemento elemento, String version) {
        String nombre = URLEncoder.encode(elemento.getNombre(), StandardCharsets.UTF_8);
        String versionActual = URLEncoder.encode(elemento.getVersion(), StandardCharsets.UTF_8);
        String versionActualizable = URLEncoder.encode(version, StandardCharsets.UTF_8);
        String baseUrl = ApplicationProperties.getProperty("api.url") + "/obtener-instrucciones";
        String urlConParametros = String.format("%s?nombre=%s&versionActual=%s&versionActualizable=%s", baseUrl, nombre, versionActual, versionActualizable);

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlConParametros))
                    .header("Content-Type", "application/json")
                    .header("Authorization", ApplicationProperties.getProperty("api.token"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                LOG.debug("Error al consultar la API: {}", response.body());
                return null;
            }

            return JsonUtils.fromJsonToList(response.body(), InstruccionResponse.class);
        } catch (Exception e) {
            LOG.error("Error al consultar instrucciones: {}", e.getMessage());
        }
        return null;
    }

    public List<InstruccionResponse> obtenerInstrucciones(String elemento, String version) {
        String nombre = URLEncoder.encode(elemento, StandardCharsets.UTF_8);
        String ultimaVersion = URLEncoder.encode(version, StandardCharsets.UTF_8);
        String baseUrl = ApplicationProperties.getProperty("api.url") + "/obtener-instrucciones-instalacion";
        String urlConParametros = String.format("%s?nombre=%s&ultimaVersion=%s", baseUrl, nombre, ultimaVersion);

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlConParametros))
                    .header("Content-Type", "application/json")
                    .header("Authorization", ApplicationProperties.getProperty("api.token"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                LOG.debug("Error al consultar la API: {}", response.body());
                return null;
            }

            return JsonUtils.fromJsonToList(response.body(), InstruccionResponse.class);
        } catch (Exception e) {
            LOG.error("Error al consultar instrucciones: {}", e.getMessage());
            return null;
        }
    }

}

package com.sideralsoft.service;

import com.sideralsoft.config.ApplicationProperties;
import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.domain.model.TipoElemento;
import com.sideralsoft.utils.JsonUtils;
import com.sideralsoft.utils.exception.ActualizacionException;
import com.sideralsoft.utils.exception.InstalacionException;
import com.sideralsoft.utils.http.ActualizacionResponse;
import com.sideralsoft.utils.http.ApiClient;
import com.sideralsoft.utils.http.InstalacionResponse;
import com.sideralsoft.utils.http.InstruccionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ConsultaService {

    private static final Logger LOG = LoggerFactory.getLogger(ConsultaService.class);

    private final ApiClient<String> apiClient;

    public ConsultaService(ApiClient<String> apiClient) {
        this.apiClient = apiClient;
    }

    public String existeActualizacionDisponible(String nombre, String version, TipoElemento tipo) throws ActualizacionException {
        String nombreTratado = URLEncoder.encode(nombre, StandardCharsets.UTF_8);
        String versionTratada = URLEncoder.encode(version, StandardCharsets.UTF_8);
        String baseUrl = ApplicationProperties.getProperty("api.url") + "/buscar-actualizacion";
        String urlConParametros = String.format("%s?nombre=%s&version=%s&tipo=%s", baseUrl, nombreTratado, versionTratada, tipo.toString());

        try {
            HttpResponse<String> response = apiClient.enviarPeticionGet(urlConParametros, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 500) {
                LOG.debug("Ha ocurrido un error en el servidor al consultar nuevas versiones para " + nombre);
                throw new ActualizacionException("Error en el servidor");
            }

            if (response.statusCode() == 200) {
                ActualizacionResponse actualizacionResponse = JsonUtils.fromJson(response.body(), ActualizacionResponse.class);

                if (!actualizacionResponse.isActualizable()) {
                    LOG.debug("No existen nuevas versiones disponibles para " + nombre);
                    return null;
                }

                LOG.debug("Existe una nueva versión del elemento {} a la version {}", nombre, actualizacionResponse.getVersion());
                return actualizacionResponse.getVersion();
            }
        } catch (Exception e) {
            LOG.error("Error al consultar la API:", e);
            throw new ActualizacionException("Error al consultar la API", e);
        }
        return null;
    }

    public InstalacionResponse existeInstalacionDisponible(String nombre, TipoElemento tipo) throws InstalacionException {
        String nombreTratado = URLEncoder.encode(nombre, StandardCharsets.UTF_8);
        String baseUrl = ApplicationProperties.getProperty("api.url") + "/buscar-recurso";
        String urlConParametros = String.format("%s?nombre=%s&tipo=%s&incluir=%s", baseUrl, nombreTratado, tipo.toString(), "procesos");

        try {
            HttpResponse<String> response = apiClient.enviarPeticionGet(urlConParametros, HttpResponse.BodyHandlers.ofString());

            LOG.debug("Respuesta de la consulta: " + response.body());

            InstalacionResponse instalacionResponse = JsonUtils.fromJson(response.body(), InstalacionResponse.class);

            if (response.statusCode() != 200) {
                LOG.debug("Ha ocurrido un error al intentar consultar la API, " + instalacionResponse.getMensaje());
                return null;
            }


            LOG.debug("Se ha encontrado el elemento {} con la version {}", nombreTratado, instalacionResponse.getVersion());
            return instalacionResponse;
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
        String urlConParametros = String.format("%s?nombre=%s&versionActual=%s&versionActualizable=%s&tipo=%s", baseUrl, nombre, versionActual, versionActualizable, TipoElemento.APLICACION);

        try {
            HttpResponse<String> response = apiClient.enviarPeticionGet(urlConParametros, HttpResponse.BodyHandlers.ofString());

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

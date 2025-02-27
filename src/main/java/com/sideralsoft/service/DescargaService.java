package com.sideralsoft.service;

import com.sideralsoft.config.ApplicationProperties;
import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.domain.model.TipoElemento;
import com.sideralsoft.utils.JsonUtils;
import com.sideralsoft.utils.http.ActualizacionRequest;
import com.sideralsoft.utils.http.ApiClient;
import com.sideralsoft.utils.http.InstruccionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DescargaService {

    private static final Logger LOG = LoggerFactory.getLogger(DescargaService.class);

    private final ApiClient<InputStream> apiClient;

    public DescargaService(ApiClient<InputStream> apiClient) {
        this.apiClient = apiClient;
    }

    public String descargarArchivos(String nombre, String version, TipoElemento tipo) {
        String rutaTemporal = null;

        String nombreTratado = URLEncoder.encode(nombre, StandardCharsets.UTF_8);
        String versionActualizable = URLEncoder.encode(version, StandardCharsets.UTF_8);
        String baseUrl = ApplicationProperties.getProperty("api.url") + "/descargar-archivos-instalacion";
        String urlConParametros = String.format("%s?nombre=%s&ultimaVersion=%s&tipo=%s", baseUrl, nombreTratado, versionActualizable, tipo);

        try {
            HttpResponse<InputStream> response = apiClient.enviarPeticionGet(urlConParametros, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() != 200) {
                LOG.debug("No se ha encontrado el elemento a descargar." + response.statusCode());
                return null;
            }

            String contentDisposition = response.headers()
                    .firstValue("Content-Disposition")
                    .orElse("");

            String nombreArchivo = Optional.of(contentDisposition)
                    .filter(header -> header.contains("filename="))
                    .map(header -> header.split("filename=")[1].replace("\"", "").trim())
                    .orElse("files.zip");

            Path rutaArchivoZIP = Paths.get(ApplicationProperties.getProperty("app.config.storage.rutaAlmacenamientoTemporal"), nombreArchivo);
            if (!Files.exists(rutaArchivoZIP)) {
                Files.createDirectories(rutaArchivoZIP);
            }

            rutaTemporal = Paths.get(ApplicationProperties.getProperty("app.config.storage.rutaAlmacenamientoTemporal"), nombre).toString();
            Files.copy(response.body(), rutaArchivoZIP, StandardCopyOption.REPLACE_EXISTING);
            descomprimirArchivoZIP(rutaArchivoZIP.toString(), rutaTemporal);

            Files.deleteIfExists(rutaArchivoZIP);
            LOG.debug("Archivos descargados y descomprimidos exitosamente en: {}", ApplicationProperties.getProperty("app.config.storage.rutaAlmacenamientoTemporal"));

        } catch (Exception e) {
            LOG.error("Ha ocurrido un error al descargar el archivo: {}", e.getMessage());
        }
        return rutaTemporal;
    }

    public String descargarArchivos(Elemento elemento, List<InstruccionResponse> instrucciones, String version) {
        String rutaTemporal = null;

        String nombre = URLEncoder.encode(elemento.getNombre(), StandardCharsets.UTF_8);
        String versionActualizable = URLEncoder.encode(version, StandardCharsets.UTF_8);
        String baseUrl = ApplicationProperties.getProperty("api.url") + "/descargar-archivos";

        ActualizacionRequest actualizacionRequest = new ActualizacionRequest();
        actualizacionRequest.setNombre(nombre);
        actualizacionRequest.setVersion(versionActualizable);
        actualizacionRequest.setInstrucciones(instrucciones);
        actualizacionRequest.setTipo(elemento.getTipo());

        try {
            HttpResponse<InputStream> response = apiClient.enviarPeticionPost(
                    baseUrl,
                    HttpRequest.BodyPublishers.ofString(JsonUtils.toJson(actualizacionRequest)),
                    HttpResponse.BodyHandlers.ofInputStream()
            );

            if (response.statusCode() != 200) {
                LOG.debug("No se ha encontrado el elemento a descargar." + response.statusCode());
                LOG.debug(response.body().toString());
                return null;
            }

            String contentDisposition = response.headers()
                    .firstValue("Content-Disposition")
                    .orElse("");

            String nombreArchivo = Optional.of(contentDisposition)
                    .filter(header -> header.contains("filename="))
                    .map(header -> header.split("filename=")[1].replace("\"", "").trim())
                    .orElse("files.zip");

            Path rutaArchivoZIP = Paths.get(ApplicationProperties.getProperty("app.config.storage.rutaAlmacenamientoTemporal"), nombreArchivo);
            if (!Files.exists(rutaArchivoZIP)) {
                Files.createDirectories(rutaArchivoZIP);
            }

            rutaTemporal = Paths.get(ApplicationProperties.getProperty("app.config.storage.rutaAlmacenamientoTemporal"), elemento.getNombre()).toString();
            Files.copy(response.body(), rutaArchivoZIP, StandardCopyOption.REPLACE_EXISTING);
            descomprimirArchivoZIP(rutaArchivoZIP.toString(), rutaTemporal);

            Files.deleteIfExists(rutaArchivoZIP);
            LOG.debug("Archivos descargados y descomprimidos exitosamente en: {}", ApplicationProperties.getProperty("app.config.storage.rutaAlmacenamientoTemporal"));

        } catch (Exception e) {
            LOG.error("Ha ocurrido un error al descargar el archivo: {}", e.getMessage());
        }
        return rutaTemporal;
    }

    private void descomprimirArchivoZIP(String rutaArchivoZIP, String directorioDestino) throws IOException {
        File rutaDestino = new File(directorioDestino);
        if (!rutaDestino.exists()) {
            rutaDestino.mkdir();
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(rutaArchivoZIP))) {
            ZipEntry entrada = zipInputStream.getNextEntry();

            while (entrada != null) {
                File rutaArchivo = new File(directorioDestino, entrada.getName());

                if (entrada.isDirectory()) {
                    if (!rutaArchivo.exists()) {
                        rutaArchivo.mkdirs();
                    }
                } else {
                    File parentDir = rutaArchivo.getParentFile();
                    if (!parentDir.exists()) {
                        parentDir.mkdirs();
                    }

                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(rutaArchivo))) {
                        byte[] bytesIn = new byte[1024];
                        int read;
                        while ((read = zipInputStream.read(bytesIn)) != -1) {
                            bos.write(bytesIn, 0, read);
                        }
                    }
                }
                zipInputStream.closeEntry();
                entrada = zipInputStream.getNextEntry();
            }
        }
    }

}

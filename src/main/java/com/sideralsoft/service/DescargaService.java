package com.sideralsoft.service;

import com.sideralsoft.config.ApplicationProperties;
import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.utils.ElementosSingleton;
import com.sideralsoft.utils.JsonUtils;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DescargaService {

    public static void main(String[] args) {
        String url = "api.url";
        String outputDirectory = "/home/jhoaho/Descargas/PruebaAPI";


        try {
            // Enviar solicitud GET para descargar el archivo zip

            //List<Elemento> elementos = ElementosSingleton.getInstance().obtenerElementos();
            String json = """
                            {
                              "nombre": "interfaz-hormolab",
                              "version": "1.0.0",
                              "hash": "45eae8f94b5b813c1802abd592c838f856b87529",
                              "tipo": "APLICACION",
                              "ruta": "C:/Sideralsoft/sistemas-externos/hormolab"
                            }
                        """;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(ApplicationProperties.getProperty("api.url") + "/buscar-actualizacion"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", ApplicationProperties.getProperty("api.token"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() == 200) {
                // Guardar el archivo zip temporalmente
                Path zipPath = Paths.get(outputDirectory, "files.zip");
                if (!Files.exists(zipPath)) {
                    Files.createDirectories(zipPath);
                }
                Files.copy(response.body(), zipPath, StandardCopyOption.REPLACE_EXISTING);

                // Descomprimir el archivo zip en la carpeta destino
                unzip(zipPath.toString(), outputDirectory);

                // Eliminar el archivo zip descargado después de extraerlo
                //Files.deleteIfExists(zipPath);
                System.out.println("Archivos descargados y descomprimidos exitosamente en: " + outputDirectory);
            } else {
                System.out.println("Error al descargar los archivos. Código de respuesta: " + response.statusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para descomprimir el archivo ZIP
    public static void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }

        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                File filePath = new File(destDirectory, entry.getName());
                if (!entry.isDirectory()) {
                    // Si es un archivo, extraerlo
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
                        byte[] bytesIn = new byte[1024];
                        int read;
                        while ((read = zipIn.read(bytesIn)) != -1) {
                            bos.write(bytesIn, 0, read);
                        }
                    }
                } else {
                    // Si es un directorio, crear la carpeta
                    filePath.mkdirs();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }

}

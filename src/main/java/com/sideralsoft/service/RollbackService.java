package com.sideralsoft.service;

import com.sideralsoft.config.ApplicationProperties;
import com.sideralsoft.utils.exception.ActualizacionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class RollbackService {

    private static final Logger LOG = LoggerFactory.getLogger(RollbackService.class);
    private final String rutaPuntoRestauracion;
    private final String nombre;

    public RollbackService(String nombre) {
        this.rutaPuntoRestauracion = ApplicationProperties.getProperty("app.config.storage.rollback");
        this.nombre = nombre;
    }

    public void generarPuntoRestauracion() throws ActualizacionException {
        Path origen = Paths.get(ApplicationProperties.getProperty("app.config.storage.rutaInstalacion"), this.nombre);
        Path rutaPuntoRestauracionAplicacion = Paths.get(this.rutaPuntoRestauracion, this.nombre);

        if (!Files.exists(rutaPuntoRestauracionAplicacion)) {
            if (!rutaPuntoRestauracionAplicacion.toFile().mkdirs()) {
                throw new ActualizacionException("No se ha podido crear un punto de restauracion");
            }
        } else {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(rutaPuntoRestauracionAplicacion)) {
                borrarArchivos(rutaPuntoRestauracionAplicacion, directoryStream);
            } catch (IOException e) {
                LOG.error("Ha ocurrido un error al intentar limpiar la carpeta de puntos de restauracion.");
                throw new ActualizacionException("Ha ocurrido un error al intentar limpiar la carpeta de puntos de restauracion.", e);
            }
        }

        this.copiarArchivos(origen, rutaPuntoRestauracionAplicacion);
    }


    public void regresarAPuntoRestauracion() throws ActualizacionException {
        Path origen = Paths.get(ApplicationProperties.getProperty("app.config.storage.rutaInstalacion"), this.nombre);
        Path rutaPuntoRestauracionAplicacion = Paths.get(this.rutaPuntoRestauracion, this.nombre);

        if (!Files.exists(rutaPuntoRestauracionAplicacion)) {
            throw new ActualizacionException("No se ha encontrado el punto de restauracion.");
        }

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(origen)) {
            borrarArchivos(origen, directoryStream);
        } catch (IOException e) {
            LOG.error("Ha ocurrido un error al intentar limpiar la carpeta de origen.");
            throw new ActualizacionException("Ha ocurrido un error al intentar limpiar la carpeta de puntos de restauracion.", e);
        }

        this.copiarArchivos(rutaPuntoRestauracionAplicacion, origen);
    }

    private void borrarArchivos(Path ruta, DirectoryStream<Path> directoryStream) throws IOException {
        if (directoryStream.iterator().hasNext()) {
            Files.walkFileTree(ruta, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                        throw exc;
                    }
                }
            });
        }
    }

    private void copiarArchivos(Path origen, Path destino) throws ActualizacionException {
        try {
            Files.walkFileTree(origen, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path carpetaDestino = destino.resolve(origen.relativize(dir));
                    if (!Files.exists(carpetaDestino)) {
                        Files.createDirectories(carpetaDestino);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path archivoDestino = destino.resolve(origen.relativize(file));
                    Files.copy(file, archivoDestino, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            LOG.error("Ha ocurrido un error durante el movimiento de archivos.", e);
            throw new ActualizacionException("Ha ocurrido un error durante el movimiento de archivos.", e);
        }
    }
}

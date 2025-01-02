package com.sideralsoft.domain;

import com.sideralsoft.config.ApplicationProperties;
import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.utils.exception.ActualizacionException;
import com.sideralsoft.utils.exception.InstalacionException;
import com.sideralsoft.utils.http.InstruccionResponse;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class Aplicacion implements Actualizable, Instalable {

    private static final Logger LOG = LoggerFactory.getLogger(Aplicacion.class);

    private final Elemento elemento;
    private final List<InstruccionResponse> instrucciones;
    private final String rutaTemporal;

    public Aplicacion(Elemento elemento, String rutaTemporal) {
        this.elemento = elemento;
        this.rutaTemporal = rutaTemporal;
        this.instrucciones = null;
    }

    public Aplicacion(Elemento elemento, String rutaTemporal, List<InstruccionResponse> instrucciones) {
        this.elemento = elemento;
        this.rutaTemporal = rutaTemporal;
        this.instrucciones = instrucciones;
    }

    @Override
    public void actualizar() throws ActualizacionException {
        try {
            this.detenerProcesos();
            this.reemplazarElementos();
            this.borrarArchivosTemporales();
            this.iniciarProcesos();
        } catch (Exception e) {
            LOG.error("Ha ocurrido un error durante la actualizacion", e);
            throw new ActualizacionException("", e);
        }
    }

    @Override
    public void detenerProcesos() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("taskkill", "/F", "/IM", elemento.getProceso());
        Process process = processBuilder.start();

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            LOG.debug("El proceso {} fue detenido exitosamente.", elemento.getProceso());
        } else {
            LOG.debug("No se pudo detener el proceso {}. Código de salida: {}", elemento.getProceso(), exitCode);
        }
    }

    @Override
    public void reemplazarElementos() throws IOException {
        assert instrucciones != null;
        for (InstruccionResponse instruccion : instrucciones) {
            Path origen = Paths.get(rutaTemporal, instruccion.getRutaInstalacion()); //TODO: Comprobar
            Path destino = Paths.get(elemento.getRuta(), instruccion.getRutaInstalacion());

            switch (instruccion.getAccion()) {
                case AGREGAR:
                    if (Files.exists(origen)) {
                        Files.move(origen, destino, StandardCopyOption.REPLACE_EXISTING);
                        LOG.debug("Archivo agregado: " + instruccion.getElemento());
                    } else {
                        LOG.debug("Archivo no encontrado para agregar: " + instruccion.getElemento());
                    }
                    break;

                case MODIFICAR:
                    if (Files.exists(origen)) {
                        Files.move(origen, destino, StandardCopyOption.REPLACE_EXISTING);
                        LOG.debug("Archivo modificado: " + instruccion.getElemento());
                    } else {
                        LOG.debug("Archivo no encontrado para modificar: " + instruccion.getElemento());
                    }
                    break;

                case ELIMINAR:
                    if (Files.exists(destino)) {
                        Files.delete(destino);
                        LOG.debug("Archivo eliminado: " + instruccion.getElemento());
                    } else {
                        LOG.debug("Archivo no encontrado para eliminar: " + instruccion.getElemento());
                    }
                    break;

                default:
                    LOG.debug("Acción no reconocida: " + instruccion.getAccion());
            }
        }
    }

    @Override
    public void borrarArchivosTemporales() {
        try {
            File directorio = new File(rutaTemporal);
            if (directorio.exists()) {
                FileUtils.deleteDirectory(directorio);
                LOG.debug("Directorio eliminado correctamente.");
            } else {
                LOG.debug("Directorio no encontrado.");
            }
        } catch (IOException e) {
            LOG.error("Ha ocurrido un error durante el borrado de archivos temporales", e);
        }
    }

    @Override
    public void iniciarProcesos() throws Exception {
        Process startProcess = new ProcessBuilder(this.elemento.getRutaProceso()).start();

        int exitCode = startProcess.waitFor();

        if (exitCode == 0) {
            LOG.debug("El proceso {} fue iniciado exitosamente.", elemento.getProceso());
        } else {
            LOG.debug("No se pudo iniciar el proceso {}. Código de salida: {}", elemento.getProceso(), exitCode);
        }
    }

    @Override
    public void instalar() throws InstalacionException {
        Path origen = Paths.get(rutaTemporal);
        Path destino = Paths.get(ApplicationProperties.getProperty("app.config.storage.rutaInstalacion"), elemento.getNombre());

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
            throw new InstalacionException("Ha ocurrido un error durante el movimiento de archivos.", e);
        } finally {
            this.borrarArchivosTemporales();
        }
    }
}

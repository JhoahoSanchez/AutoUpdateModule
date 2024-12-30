package com.sideralsoft.domain;

import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.utils.exception.ActualizacionException;
import com.sideralsoft.utils.http.InstruccionResponse;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class Aplicacion implements Actualizable {

    private static final Logger LOG = LoggerFactory.getLogger(Aplicacion.class);

    private final Elemento elemento;
    private final List<InstruccionResponse> instrucciones;
    private final String rutaTemporal;


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
        for (InstruccionResponse instruccion : instrucciones) {
            Path sourcePath = Paths.get(rutaTemporal, instruccion.getRutaInstalacion()); //TODO: Comprobar
            Path targetPath = Paths.get(elemento.getRuta(), instruccion.getRutaInstalacion());

            switch (instruccion.getAccion()) {
                case AGREGAR:
                    if (Files.exists(sourcePath)) {
                        Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        LOG.debug("Archivo agregado: " + instruccion.getElemento());
                    } else {
                        LOG.debug("Archivo no encontrado para agregar: " + instruccion.getElemento());
                    }
                    break;

                case MODIFICAR:
                    if (Files.exists(sourcePath)) {
                        Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        LOG.debug("Archivo modificado: " + instruccion.getElemento());
                    } else {
                        LOG.debug("Archivo no encontrado para modificar: " + instruccion.getElemento());
                    }
                    break;

                case ELIMINAR:
                    if (Files.exists(targetPath)) {
                        Files.delete(targetPath);
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
    public void borrarArchivosTemporales() throws Exception {
        File directorio = new File(rutaTemporal);
        if (directorio.exists()) {
            FileUtils.deleteDirectory(directorio);
            LOG.debug("Directorio eliminado correctamente.");
        } else {
            LOG.debug("Directorio no encontrado.");
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
}

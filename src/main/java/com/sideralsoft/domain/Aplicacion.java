package com.sideralsoft.domain;

import com.sideralsoft.config.ApplicationProperties;
import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.domain.model.Proceso;
import com.sideralsoft.service.RollbackService;
import com.sideralsoft.utils.exception.ActualizacionException;
import com.sideralsoft.utils.exception.InstalacionException;
import com.sideralsoft.utils.http.InstruccionResponse;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class Aplicacion implements Actualizable, Instalable {

    private static final Logger LOG = LoggerFactory.getLogger(Aplicacion.class);

    protected final Elemento elemento;
    protected final List<InstruccionResponse> instrucciones;
    protected final String rutaTemporal;

    private final RollbackService rollbackService;

    public Aplicacion(Elemento elemento, String rutaTemporal) {
        this.elemento = elemento;
        this.rutaTemporal = rutaTemporal;
        this.instrucciones = null;

        this.rollbackService = null;
    }

    public Aplicacion(Elemento elemento, String rutaTemporal, List<InstruccionResponse> instrucciones) {
        this.elemento = elemento;
        this.rutaTemporal = rutaTemporal;
        this.instrucciones = instrucciones;

        this.rollbackService = new RollbackService(elemento.getNombre());
    }

    @Override
    public void actualizar() throws ActualizacionException {
        try {
            this.detenerProcesos();
        } catch (Exception e) {
            LOG.error("Ha ocurrido un error al detener los procesos.", e);
            throw new ActualizacionException("Ha ocurrido un error al detener los procesos.", e);
        }

        try {
            this.rollbackService.generarPuntoRestauracion();
            this.reemplazarElementos();
            this.borrarArchivosTemporales();
            this.iniciarProcesos();
        } catch (Exception e) {
            LOG.error("Ha ocurrido un error durante la actualizacion", e);
            this.rollbackService.regresarAPuntoRestauracion();
            throw new ActualizacionException("Ha ocurrido un error durante la actualizacion", e);
        }
    }

    @Override
    public void detenerProcesos() throws IOException, InterruptedException, ActualizacionException {
        if (elemento.getProcesos() == null) {
            return;
        }

        for (Proceso proceso : elemento.getProcesos()) {
            boolean enEjecucion = procesoEnEjecucion(proceso.getNombre());

            if (!enEjecucion) {
                LOG.debug("El proceso {} no está en ejecución. No es necesario detenerlo.", proceso.getNombre());
                continue;
            }

            ProcessBuilder processBuilder = new ProcessBuilder("taskkill", "/F", "/IM", proceso.getNombre());
            Process process = processBuilder.start();

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                LOG.debug("El proceso {} fue detenido exitosamente.", proceso.getNombre());
            } else {
                LOG.debug("No se pudo detener el proceso {}. Código de salida: {}", proceso.getNombre(), exitCode);
                throw new ActualizacionException("No se pudo detener el proceso " + proceso.getNombre() + ". Código de salida: " + exitCode);
            }
        }
    }

    @Override
    public void reemplazarElementos() throws IOException, ActualizacionException {
        assert instrucciones != null;
        for (InstruccionResponse instruccion : instrucciones) {
            Path origen = Paths.get(rutaTemporal, instruccion.getRuta());
            Path destino = Paths.get(elemento.getRuta(), instruccion.getRuta());

            switch (instruccion.getAccion()) {
                case AGREGAR:
                    if (Files.exists(origen)) {
                        Path directorioDestino = destino.getParent();
                        if (!Files.exists(directorioDestino)) {
                            try {
                                Files.createDirectories(directorioDestino);
                                LOG.debug("Directorio creado: " + directorioDestino);
                            } catch (IOException e) {
                                LOG.error("Error al crear el directorio de destino: " + directorioDestino, e);
                                throw new ActualizacionException("Error al crear el directorio de destino: " + directorioDestino, e);
                            }
                        }

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
            if (rutaTemporal.isEmpty()) {
                return;
            }

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
        if (elemento.getProcesos() == null) {
            return;
        }

        for (Proceso proceso : elemento.getProcesos()) {
            try {
                Process iniciarProceso = new ProcessBuilder(proceso.getRuta()).start();
                LOG.debug("El proceso {} fue iniciado exitosamente.", proceso.getNombre());
            } catch (IOException e) {
                LOG.error("Error al iniciar el proceso {}: {}", proceso.getNombre(), e.getMessage());
                throw new ActualizacionException("No se pudo iniciar el proceso " + proceso.getNombre(), e);
            }
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

    private boolean procesoEnEjecucion(String nombreProceso) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("tasklist", "/FI", "IMAGENAME eq " + nombreProceso);
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IOException("Error al verificar si el proceso está en ejecución: " + nombreProceso);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(nombreProceso)) {
                    return true;
                }
            }
        }

        return false;
    }
}

package com.sideralsoft.domain;

import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.domain.model.Proceso;
import com.sideralsoft.service.RollbackService;
import com.sideralsoft.utils.exception.ActualizacionException;
import com.sideralsoft.utils.exception.InstalacionException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class Instalador implements Actualizable, Instalable {

    private final static Logger LOG = LoggerFactory.getLogger(Instalador.class);
    private final RollbackService rollbackService;

    private final Elemento elemento;
    private final String rutaTemporal;

    public Instalador(Elemento elemento, String rutaTemporal) {
        this.elemento = elemento;
        this.rutaTemporal = rutaTemporal;
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
    public void detenerProcesos() throws Exception {
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
    public void reemplazarElementos() throws Exception {
        ProcessBuilder processBuilderDesinstalacion = new ProcessBuilder(Path.of(elemento.getRuta(), "unins000.exe").toString(), "/VERYSILENT");

        Process procesoDesinstalacion = processBuilderDesinstalacion.start();

        int codigoSalidaDesinstalacion = procesoDesinstalacion.waitFor();
        if (codigoSalidaDesinstalacion != 0) {
            throw new ActualizacionException("No se ha podido desinstalar " + elemento.getNombre() + " correctamente");
        }

        ProcessBuilder processBuilderInstalacion = new ProcessBuilder(Path.of(rutaTemporal, elemento.getNombre() + "-setup.exe", "/VERYSILENT").toString());
        Process procesoInstalacion = processBuilderInstalacion.start();

        int codigoSalidaInstalacion = procesoInstalacion.waitFor();

        if (codigoSalidaInstalacion != 0) {
            throw new ActualizacionException("No se ha podido instalar " + elemento.getNombre());
        }

        LOG.debug(elemento.getNombre() + " instalada correctamenta");
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
        try {
            ProcessBuilder processBuilderInstalacion = new ProcessBuilder(Path.of(rutaTemporal, elemento.getNombre() + "-setup.exe").toString(), "/VERYSILENT");
            Process procesoInstalacion = processBuilderInstalacion.start();

            int codigoSalidaInstalacion = procesoInstalacion.waitFor();

            if (codigoSalidaInstalacion != 0) {
                throw new InstalacionException("No se ha podido instalar " + elemento.getNombre());
            }

            LOG.debug(elemento.getNombre() + " instalada correctamenta");
        } catch (Exception e) {
            LOG.error("Ha ocurrido un error durante la instalacion", e);
            throw new InstalacionException("Ha ocurrido un error durante la instalacion", e);
        } finally {
            this.borrarArchivosTemporales();
        }
    }

    @Override
    public void instalarExtras() throws InstalacionException {

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

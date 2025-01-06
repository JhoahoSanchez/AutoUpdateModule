package com.sideralsoft.service;

import com.sideralsoft.utils.exception.ActualizacionException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class CertificadoService {

    private final static Logger LOG = LoggerFactory.getLogger(CertificadoService.class);

    public void instalarCertificado(String rutaDescarga) throws ActualizacionException {
        try {
            File carpeta = new File(rutaDescarga);
            String rutaCertificado = Objects.requireNonNull(carpeta.listFiles())[0].getAbsolutePath();

            if (StringUtils.isBlank(rutaCertificado)) {
                throw new ActualizacionException("No se ha encontrado el certificado a instalar.");
            }

            ProcessBuilder importarCertificado = new ProcessBuilder(
                    "cmd.exe", "/c", "certutil", "-addstore", "Root", rutaCertificado
            );
            this.ejecutarProceso(importarCertificado, "Importando nuevo certificado...");

            LOG.debug("Certificado instalado exitosamente.");
        } catch (Exception e) {
            LOG.error("Error al instalar el certificado", e);
            throw new ActualizacionException("Error al instalar el certificado", e);
        }
    }

    public void actualizarCertificado(String rutaDescarga, String alias) throws ActualizacionException {
        try {
            File carpeta = new File(rutaDescarga);
            String rutaCertificado = Objects.requireNonNull(carpeta.listFiles())[0].getAbsolutePath();

            if (StringUtils.isBlank(rutaCertificado)) {
                throw new ActualizacionException("No se ha encontrado el certificado a actualizar.");
            }

            ProcessBuilder eliminarCertificado = new ProcessBuilder(
                    "cmd.exe", "/c", "certutil", "-delstore", "Root", alias
            );
            this.ejecutarProceso(eliminarCertificado, "Eliminando certificado existente...");

            ProcessBuilder importarCertificado = new ProcessBuilder(
                    "cmd.exe", "/c", "certutil", "-addstore", "Root", rutaCertificado
            );
            this.ejecutarProceso(importarCertificado, "Importando nuevo certificado...");

            LOG.debug("Certificado actualizado exitosamente.");

        } catch (Exception e) {
            LOG.error("Error al actualizar certificado", e);
            throw new ActualizacionException("Error al actualizar certificado", e);
        }
    }

    private void ejecutarProceso(ProcessBuilder processBuilder, String mensaje) throws IOException, InterruptedException {
        LOG.debug(mensaje);
        processBuilder.inheritIO();
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            LOG.error("Ha ocurrido un error al ejecutar el processo. Codigo de salida: {}", exitCode);
            throw new RuntimeException("El comando falló con código de salida: " + exitCode);
        }
    }
}

package com.sideralsoft.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CertificadoService {

    private final static Logger LOG = LoggerFactory.getLogger(CertificadoService.class);

    public void instalarCertificado(String rutaDescarga) throws IOException, InterruptedException {
        ProcessBuilder importarCertificado = new ProcessBuilder(
                "cmd.exe", "/c", "certutil", "-addstore", "Root", rutaDescarga
        );
        this.ejecutarProceso(importarCertificado, "Importando nuevo certificado...");

        LOG.debug("Certificado instalado exitosamente.");
    }

    public void actualizarCertificado(String rutaDescarga, String alias) {
        try {
            ProcessBuilder eliminarCertificado = new ProcessBuilder(
                    "cmd.exe", "/c", "certutil", "-delstore", "Root", alias
            );
            this.ejecutarProceso(eliminarCertificado, "Eliminando certificado existente...");

            ProcessBuilder importarCertificado = new ProcessBuilder(
                    "cmd.exe", "/c", "certutil", "-addstore", "Root", rutaDescarga
            );
            this.ejecutarProceso(importarCertificado, "Importando nuevo certificado...");

            LOG.debug("Certificado actualizado exitosamente.");

        } catch (Exception e) {
            LOG.error("Error al actualizar certificado", e);
            //TODO: Logica para ejecutar la tarea en X tiempo
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

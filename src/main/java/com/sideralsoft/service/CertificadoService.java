package com.sideralsoft.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CertificadoService {

    private final static Logger LOG = LoggerFactory.getLogger(CertificadoService.class);

//    public void comprobarValidezCertificados() { //Logica para forzar actualizacion, comprobar
//        try {
//            KeyStore windowsKeyStore = KeyStore.getInstance("Windows-ROOT");
//            windowsKeyStore.load(null, null);
//
//            Enumeration<String> aliases = windowsKeyStore.aliases();
//            while (aliases.hasMoreElements()) {
//                String alias = aliases.nextElement();
//
//                if (windowsKeyStore.isCertificateEntry(alias) && alias.equals(CertificadoService.alias)) {
//                    X509Certificate certificado = (X509Certificate) windowsKeyStore.getCertificate(alias);
//                    long tiempoMinimo = 30L * 24L * 60L * 60L * 1000L; // 30 días en milisegundos
//                    Date fechaActual = new Date();
//                    long tiempoRestante = certificado.getNotAfter().getTime() - fechaActual.getTime();
//
//                    if (tiempoRestante <= tiempoMinimo) {
//                        System.out.println("El certificado expira pronto. Debería actualizarse."); //reemplazar por log4j
//                        this.actualizarCertificado();
//                    } else {
//                        System.out.println("El certificado es válido."); //reemplazar por log4j
//                    }
//                    System.out.println("--------------------------------------------------"); //reemplazar por log4j
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace(); //reemplazar por log4j
//        }
//    }

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

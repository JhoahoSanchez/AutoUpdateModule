package com.sideralsoft.certificados;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;

public class DetalleCertificados {

    public static final String alias = "Sideralsoft";

    public void getInfoCertificados() {
        try {
            KeyStore windowsKeyStore = KeyStore.getInstance("Windows-ROOT");
            windowsKeyStore.load(null, null);

            Enumeration<String> aliases = windowsKeyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();

                // Verificar si es un certificado
                if (windowsKeyStore.isCertificateEntry(alias)) {
                    X509Certificate cert = (X509Certificate) windowsKeyStore.getCertificate(alias);
                    System.out.println("Certificado Alias: " + alias);

                    // Mostrar detalles del certificado
                    System.out.println("Emitido a: " + cert.getSubjectDN().getName());
                    System.out.println("Válido desde: " + cert.getNotBefore());
                    System.out.println("Válido hasta: " + cert.getNotAfter());

                    // Calcular si el certificado expira pronto (en 30 días, por ejemplo)
                    long expirationThreshold = 30L * 24L * 60L * 60L * 1000L; // 30 días en milisegundos
                    Date currentDate = new Date();
                    long remainingTime = cert.getNotAfter().getTime() - currentDate.getTime();

                    if (remainingTime <= expirationThreshold) {
                        System.out.println("El certificado expira pronto. Debería actualizarse.");
                        // Lógica para actualizar el certificado
                        //actualizarCertificado(alias);
                    } else {
                        System.out.println("El certificado es válido.");
                    }
                    System.out.println("--------------------------------------------------");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void comprobarValidezCertificados() {
        try {
            KeyStore windowsKeyStore = KeyStore.getInstance("Windows-ROOT");
            windowsKeyStore.load(null, null);

            Enumeration<String> aliases = windowsKeyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();

                if (windowsKeyStore.isCertificateEntry(alias) && alias.equals(DetalleCertificados.alias)) {
                    X509Certificate certificado = (X509Certificate) windowsKeyStore.getCertificate(alias);
                    long tiempoMinimo = 30L * 24L * 60L * 60L * 1000L; // 30 días en milisegundos
                    Date fechaActual = new Date();
                    long tiempoRestante = certificado.getNotAfter().getTime() - fechaActual.getTime();

                    if (tiempoRestante <= tiempoMinimo) {
                        System.out.println("El certificado expira pronto. Debería actualizarse."); //reemplazar por log4j
                        this.actualizarCertificado();
                    } else {
                        System.out.println("El certificado es válido."); //reemplazar por log4j
                    }
                    System.out.println("--------------------------------------------------"); //reemplazar por log4j
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); //reemplazar por log4j
        }
    }

    private void actualizarCertificado() {

        //TODO: Implementar endpoint para descarga del certificado desde el repositorio en la nube

        try {
            String command = "cmd.exe /c \"D:\\Universidad\\Tesis\\AutoUpdateV1.0.0\\AutoUpdateModule_2.0\\AutoUpdateModule\\src\\main\\resources\\Scripts\\actualizar_certificado.bat";
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("El script se ejecutó correctamente.");
            } else {
                System.out.println("Hubo un error al ejecutar el script.");
            }

        } catch (Exception e) {
            e.printStackTrace(); //Cambiar por log4j
        }
    }

}

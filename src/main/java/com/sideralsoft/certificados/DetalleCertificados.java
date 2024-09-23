package com.sideralsoft.certificados;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;

public class DetalleCertificados {

    public void getInfoCertificados() {
        try {
            // Cargar el almacén de certificados de Windows (My Store)
            KeyStore windowsKeyStore = KeyStore.getInstance("Windows-ROOT");
            windowsKeyStore.load(null, null);

            // Obtener todos los alias (nombres) de los certificados en el almacén
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

    // Este método asume que tienes un nuevo certificado en un archivo y lo reemplaza en el almacén
    public void actualizarCertificado(String alias) {
        try {
            // Cargar el almacén de certificados de Windows (My Store)
            KeyStore windowsKeyStore = KeyStore.getInstance("Windows-MY");
            windowsKeyStore.load(null, null);

            // Ruta del nuevo certificado (simulado)
            String rutaNuevoCertificado = "ruta/al/nuevo/certificado.crt";

            // Cargar el nuevo certificado desde el archivo
            FileInputStream fis = new FileInputStream(rutaNuevoCertificado);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate nuevoCert = (X509Certificate) cf.generateCertificate(fis);
            fis.close();

            // Mostrar información del nuevo certificado
            System.out.println("Nuevo certificado emitido a: " + nuevoCert.getSubjectDN().getName());
            System.out.println("Válido desde: " + nuevoCert.getNotBefore());
            System.out.println("Válido hasta: " + nuevoCert.getNotAfter());

            // Reemplazar el certificado en el almacén
            windowsKeyStore.setCertificateEntry(alias, nuevoCert);

            // Guardar el almacén de vuelta (si es necesario)
            // Si fuera un archivo JKS o PKCS12, tendrías que guardarlo, pero en Windows no es necesario
            // windowsKeyStore.store(new FileOutputStream("ruta/al/almacen.jks"), "password".toCharArray());

            System.out.println("Certificado actualizado exitosamente.");

        } catch (Exception e) {
            System.out.println("Error al actualizar el certificado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package com.sideralsoft.certificados;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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
            KeyStore windowsKeyStore = KeyStore.getInstance("Windows-ROOT");
            windowsKeyStore.load(null, null);

            // Mostrar los alias existentes para ver si el alias existe
            System.out.println("Alias disponibles en el almacén:");
            for (String a : java.util.Collections.list(windowsKeyStore.aliases())) {
                System.out.println("- " + a);
            }

            String rutaNuevoCertificado = "D:\\Universidad\\Tesis\\AutoUpdateV1.0.0\\AutoUpdateModule_2.0\\AutoUpdateModule\\src\\main\\resources\\Certificados\\server700.crt";

            FileInputStream fis = new FileInputStream(rutaNuevoCertificado);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate nuevoCert = (X509Certificate) cf.generateCertificate(fis);
            fis.close();

            System.out.println("Nuevo certificado emitido a: " + nuevoCert.getSubjectX500Principal().toString());
            System.out.println("Válido desde: " + nuevoCert.getNotBefore());
            System.out.println("Válido hasta: " + nuevoCert.getNotAfter());

            if (windowsKeyStore.isCertificateEntry(alias)) {
                windowsKeyStore.setCertificateEntry(alias, nuevoCert);
                System.out.println("Certificado actualizado exitosamente.");
            } else {
                System.out.println("El alias especificado no existe en el almacén de claves.");
            }

        } catch (Exception e) {
            System.out.println("Error al actualizar el certificado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void eliminarCertificado(String alias) {
        String certificado = "Sideralsoft"; // Cambia esto por el nombre del certificado

        try {
            // Construir el comando
            String command = String.format("certutil -delstore ROOT \"%s\"", certificado);
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            Process process = processBuilder.start();

            // Leer la salida
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Esperar a que el proceso termine
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Certificado eliminado exitosamente.");
            } else {
                System.out.println("Error al eliminar el certificado.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarYActualizarCertificado() {
        try {
            // Comando para ejecutar el archivo por lotes
            String command = "cmd.exe /c \"D:\\Universidad\\Tesis\\AutoUpdateV1.0.0\\AutoUpdateModule_2.0\\AutoUpdateModule\\src\\main\\resources\\Scripts\\actualizar_certificado.bat";
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            Process process = processBuilder.start();

            // Leer la salida
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Esperar a que el proceso termine
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("El script se ejecutó correctamente.");
            } else {
                System.out.println("Hubo un error al ejecutar el script.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*

    import java.io.BufferedReader;
import java.io.InputStreamReader;

public class EliminarCertificado {

    public static void main(String[] args) {
        String certificado = "Nombre o Alias del Certificado"; // Cambia esto por el nombre del certificado

        try {
            // Crear un archivo por lotes para eliminar el certificado
            String batchCommand = String.format("echo certutil -delstore Root \"%s\" > eliminar_certificado.bat", certificado);
            ProcessBuilder batchBuilder = new ProcessBuilder("cmd.exe", "/c", batchCommand);
            batchBuilder.start();

            // Ejecutar el archivo por lotes con privilegios elevados
            String command = "runas /user:administrador cmd /c eliminar_certificado.bat";
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            Process process = processBuilder.start();

            // Leer la salida
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Esperar a que el proceso termine
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Certificado eliminado exitosamente.");
            } else {
                System.out.println("Error al eliminar el certificado.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



     */


    /*

    @echo off

REM Ruta del nuevo certificado
set CERT_PATH=D:\Universidad\Tesis\AutoUpdateV1.0.0\AutoUpdateModule_2.0\AutoUpdateModule\src\main\resources\Certificados\server700.crt

REM Alias del certificado que deseas actualizar
set ALIAS=nombre_del_certificado

REM Eliminar el certificado existente (sin confirmación)
certutil -delstore Root "%ALIAS%"

REM Importar el nuevo certificado
certutil -addstore Root "%CERT_PATH%"

echo Certificado actualizado exitosamente.
pause


     */


    /*

    import java.io.BufferedReader;
import java.io.InputStreamReader;

public class EliminarYActualizarCertificado {

    public static void main(String[] args) {
        try {
            // Comando para ejecutar el archivo por lotes
            String command = "cmd.exe /c \"D:\\ruta\\a\\tu\\archivo\\actualizar_certificado.bat\"";
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            Process process = processBuilder.start();

            // Leer la salida
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Esperar a que el proceso termine
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("El script se ejecutó correctamente.");
            } else {
                System.out.println("Hubo un error al ejecutar el script.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


     */
}

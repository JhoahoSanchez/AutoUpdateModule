package com.sideralsoft.domain;

import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.service.CertificadoService;
import com.sideralsoft.utils.exception.ActualizacionException;
import com.sideralsoft.utils.exception.InstalacionException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Certificado implements Actualizable, Instalable {

    private static final Logger LOG = LoggerFactory.getLogger(Certificado.class);

    private final Elemento elemento;
    private final String rutaDescarga;
    private final CertificadoService certificadoService;

    public Certificado(Elemento elemento, String rutaDescarga) {
        this.elemento = elemento;
        this.rutaDescarga = rutaDescarga;
        this.certificadoService = new CertificadoService();
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
            throw new ActualizacionException("Ha ocurrido un error durante la actualizacion", e);
        }
    }

    @Override
    public void detenerProcesos() {
    }

    @Override
    public void reemplazarElementos() throws ActualizacionException {
        this.certificadoService.actualizarCertificado(rutaDescarga, elemento.getNombre());
    }

    @Override
    public void borrarArchivosTemporales() throws IOException {
        File directorio = new File(rutaDescarga);
        if (directorio.exists()) {
            FileUtils.deleteDirectory(directorio);
            LOG.debug("Directorio eliminado correctamente.");
        } else {
            LOG.debug("Directorio no encontrado.");
        }
    }

    @Override
    public void iniciarProcesos() {
    }

    @Override
    public void instalar() throws InstalacionException {
        try {
            this.certificadoService.instalarCertificado(rutaDescarga);
        } catch (Exception e) {
            LOG.error("Ha ocurrido un error durante la instalacion de certificados.", e);
            throw new InstalacionException("Ha ocurrido un error durante la instalacion de certificados.", e);
        }
    }
}

package com.sideralsoft.service;

import com.sideralsoft.domain.Aplicacion;
import com.sideralsoft.domain.Certificado;
import com.sideralsoft.domain.Instalable;
import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.domain.model.TipoElemento;
import com.sideralsoft.utils.exception.InstalacionException;
import com.sideralsoft.utils.http.ApiClientImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class InstalacionService {

    private static final Logger LOG = LoggerFactory.getLogger(InstalacionService.class);

    private final DescargaService descargaService;

    public InstalacionService() {
        descargaService = new DescargaService(new ApiClientImpl<InputStream>());
    }

    public boolean instalarElemento(Elemento elemento, String version) {
        String rutaTemporal = descargaService.descargarArchivos(elemento, version);

        if (StringUtils.isBlank(rutaTemporal)) {
            return false;
        }

        try {
            Instalable instalable;
            if (elemento.getTipo().equals(TipoElemento.APLICACION)) {
                instalable = new Aplicacion(elemento, rutaTemporal);
                instalable.instalar();
                return true;
            }

            if (elemento.getTipo().equals(TipoElemento.CERTIFICADO)) {
                instalable = new Certificado(elemento, rutaTemporal);
                instalable.instalar();
                return true;
            }
        } catch (InstalacionException e) {
            LOG.error("Error al actualizar el elemento " + elemento.getNombre(), e);
            return false;
        }
        return false;
    }
}

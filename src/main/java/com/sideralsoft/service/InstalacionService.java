package com.sideralsoft.service;

import com.sideralsoft.domain.Aplicacion;
import com.sideralsoft.domain.Certificado;
import com.sideralsoft.domain.Instalable;
import com.sideralsoft.domain.Instalador;
import com.sideralsoft.domain.model.Dependencia;
import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.domain.model.TipoElemento;
import com.sideralsoft.utils.exception.InstalacionException;
import com.sideralsoft.utils.http.ApiClientImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstalacionService {

    private static final Logger LOG = LoggerFactory.getLogger(InstalacionService.class);

    private final DescargaService descargaService;

    public InstalacionService() {
        descargaService = new DescargaService(new ApiClientImpl<>());
    }

    public boolean instalarElemento(Elemento elemento, String version, Dependencia dependencia) {
        try {
            Instalable instalable;

            if (dependencia != null) {
                instalable = new Aplicacion(elemento, dependencia);
                instalable.instalarExtras();
                return true;
            }

            String rutaTemporal = descargaService.descargarArchivos(elemento.getNombre(), version, elemento.getTipo());

            if (StringUtils.isBlank(rutaTemporal)) {
                return false;
            }

            if (elemento.getTipo().equals(TipoElemento.APLICACION)) {
                instalable = new Aplicacion(elemento, rutaTemporal);
                instalable.instalar();
                return true;
            }

            if (elemento.getTipo().equals(TipoElemento.INSTALADOR)) {
                instalable = new Instalador(elemento, rutaTemporal);
                instalable.instalar();
                return true;
            }

            if (elemento.getTipo().equals(TipoElemento.CERTIFICADO)) {
                instalable = new Certificado(elemento, rutaTemporal);
                instalable.instalar();
                return true;
            }
        } catch (InstalacionException e) {
            LOG.error("Error al instalar el elemento " + elemento.getNombre(), e);
            return false;
        }
        return false;
    }
}

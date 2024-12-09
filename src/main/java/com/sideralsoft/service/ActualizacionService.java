package com.sideralsoft.service;

import com.sideralsoft.domain.Actualizable;
import com.sideralsoft.domain.Aplicacion;
import com.sideralsoft.domain.Certificado;
import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.domain.model.TipoElemento;
import com.sideralsoft.utils.ElementosSingleton;
import com.sideralsoft.utils.http.InstruccionResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ActualizacionService {

    private static final Logger LOG = LoggerFactory.getLogger(ActualizacionService.class);

    private final DescargaService descargaService;

    public ActualizacionService() {
        this.descargaService = new DescargaService();
    }

    public void actualizarElementos() {
        List<Elemento> elementos = ElementosSingleton.getInstance().obtenerElementos();

        for (Elemento elemento : elementos) {
            String rutaTemporal = descargaService.descargarArchivos(elemento);
            if (rutaTemporal == null) {
                continue;
            }

            Actualizable actualizable;
            if (elemento.getTipo().equals(TipoElemento.APLICACION)) {
                //actualizable = new Aplicacion(elemento, rutaTemporal);
                //actualizable.actualizar();
                continue;
            }

            if (elemento.getTipo().equals(TipoElemento.CERTIFICADO)) {
                actualizable = new Certificado(elemento, rutaTemporal);
                actualizable.actualizar();
            }
        }
    }

    public void actualizarElemento(Elemento elemento, List<InstruccionResponse> instrucciones, String version) {
        String rutaTemporal = descargaService.descargarArchivos(elemento, instrucciones, version);

        if (!StringUtils.isNotBlank(rutaTemporal)) {
            return;
        }

        Actualizable actualizable;
        if (elemento.getTipo().equals(TipoElemento.APLICACION)) {
            actualizable = new Aplicacion(elemento, rutaTemporal, instrucciones);
            actualizable.actualizar();
            return;
        }

        if (elemento.getTipo().equals(TipoElemento.CERTIFICADO)) {
            actualizable = new Certificado(elemento, rutaTemporal);
            actualizable.actualizar();
        }
    }
}

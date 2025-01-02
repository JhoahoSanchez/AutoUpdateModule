package com.sideralsoft.service;

import com.sideralsoft.domain.Actualizable;
import com.sideralsoft.domain.Aplicacion;
import com.sideralsoft.domain.Certificado;
import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.domain.model.TipoElemento;
import com.sideralsoft.utils.exception.ActualizacionException;
import com.sideralsoft.utils.http.InstruccionResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ActualizacionService {

    private static final Logger LOG = LoggerFactory.getLogger(ActualizacionService.class);

    private final DescargaService descargaService;

    public ActualizacionService() {
        this.descargaService = new DescargaService();
    }

//    public void actualizarElementos() {
//        List<Elemento> elementos = ElementosSingleton.getInstance().obtenerElementos();
//
//        for (Elemento elemento : elementos) {
//            String rutaTemporal = descargaService.descargarArchivos(elemento);
//            if (rutaTemporal == null) {
//                continue;
//            }
//
//            Actualizable actualizable;
//            if (elemento.getTipo().equals(TipoElemento.APLICACION)) {
//                //actualizable = new Aplicacion(elemento, rutaTemporal);
//                //actualizable.actualizar();
//                continue;
//            }
//
//            if (elemento.getTipo().equals(TipoElemento.CERTIFICADO)) {
//                actualizable = new Certificado(elemento, rutaTemporal);
//                //actualizable.actualizar();
//            }
//        }
//    }

    public boolean actualizarElemento(Elemento elemento, List<InstruccionResponse> instrucciones, String version) {
        String rutaTemporal = descargaService.descargarArchivos(elemento, instrucciones, version);

        if (StringUtils.isBlank(rutaTemporal)) {
            return false;
        }

        try {
            Actualizable actualizable;
            if (elemento.getTipo().equals(TipoElemento.APLICACION)) {
                actualizable = new Aplicacion(elemento, rutaTemporal, instrucciones);
                actualizable.actualizar();
                return true;
            }

            if (elemento.getTipo().equals(TipoElemento.CERTIFICADO)) {
                actualizable = new Certificado(elemento, rutaTemporal);
                actualizable.actualizar();
                return true;
            }

            if (elemento.getTipo().equals(TipoElemento.APLICACION_GESTOR)) {
                RollbackService rollbackService = new RollbackService(elemento.getNombre());
                rollbackService.generarPuntoRestauracion();

                new ProcessBuilder("java", "-jar", "updater.jar").start();
                System.exit(0);
            }
        } catch (ActualizacionException e) {
            LOG.error("Error al actualizar el elemento " + elemento.getNombre(), e);
            return false;
        } catch (IOException e) {
            LOG.error("Ha ocurrido un error general al intentar actualizar el elemento " + elemento.getNombre(), e);
            return false;
        }
        return false;
    }
}

package com.sideralsoft.service;

import com.sideralsoft.domain.*;
import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.domain.model.TipoElemento;
import com.sideralsoft.utils.exception.ActualizacionException;
import com.sideralsoft.utils.http.InstruccionResponse;
import com.sideralsoft.utils.http.TipoAccion;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ActualizacionService {

    private static final Logger LOG = LoggerFactory.getLogger(ActualizacionService.class);

    private final DescargaService descargaService;

    public ActualizacionService(DescargaService descargaService) {
        this.descargaService = descargaService;
    }

    public boolean actualizarElemento(Elemento elemento, List<InstruccionResponse> instrucciones, String version) {
        String rutaTemporal;

        if (instrucciones != null && !instrucciones.stream().allMatch(instr -> instr.getAccion() == TipoAccion.ELIMINAR)) {
            rutaTemporal = descargaService.descargarArchivos(elemento, instrucciones, version);
        } else {
            rutaTemporal = descargaService.descargarArchivos(elemento.getNombre(), elemento.getVersion(), elemento.getTipo());
        }

        if (StringUtils.isBlank(rutaTemporal)) {
            return false;
        }

        try { //TODO: AGREGAR SOPORTE PARA DEPENDENCIA
            Actualizable actualizable;
            if (elemento.getTipo().equals(TipoElemento.APLICACION)) {
                actualizable = new Aplicacion(elemento, rutaTemporal, instrucciones);
                actualizable.actualizar();
                return true;
            }

            if (elemento.getTipo().equals(TipoElemento.INSTALADOR)) {
                actualizable = new Instalador(elemento, rutaTemporal);
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

                actualizable = new GestorActualizaciones(elemento, rutaTemporal, instrucciones);
                actualizable.actualizar();
            }
        } catch (ActualizacionException e) {
            LOG.error("Error al actualizar el elemento " + elemento.getNombre(), e);
            return false;
        }
        return false;
    }
}

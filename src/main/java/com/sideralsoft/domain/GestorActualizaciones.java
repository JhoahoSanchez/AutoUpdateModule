package com.sideralsoft.domain;

import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.utils.exception.ActualizacionException;
import com.sideralsoft.utils.http.InstruccionResponse;

import java.util.List;

public class GestorActualizaciones extends Aplicacion {

    public GestorActualizaciones(Elemento elemento, String ruta, List<InstruccionResponse> instrucciones) {
        super(elemento, ruta, instrucciones);
    }

    @Override
    public void actualizar() throws ActualizacionException {
        super.actualizar(); //TODO: MODIFICAR EL COMPORTAMIENTO
    }
}

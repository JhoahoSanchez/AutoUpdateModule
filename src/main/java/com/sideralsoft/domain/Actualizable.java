package com.sideralsoft.domain;

import com.sideralsoft.utils.exception.ActualizacionException;

public interface Actualizable {

    void actualizar() throws ActualizacionException;

    void detenerProcesos() throws Exception;

    void reemplazarElementos() throws Exception;

    void borrarArchivosTemporales() throws Exception;

    void iniciarProcesos() throws Exception;

}

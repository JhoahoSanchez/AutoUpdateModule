package com.sideralsoft.domain;

public interface Actualizable {

    void actualizar();

    void detenerProcesos() throws Exception;

    void reemplazarElementos() throws Exception;

    void borrarArchivosTemporales() throws Exception;

    void iniciarProcesos() throws Exception;

}

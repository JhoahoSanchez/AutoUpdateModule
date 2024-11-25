package com.sideralsoft.domain;

import com.sideralsoft.domain.model.Elemento;

public interface Actualizable {

    void actualizar();

    void detenerProcesos();

    void reemplazarElementos();

    void borrarArchivosTemporales();

    void iniciarProcesos();

}

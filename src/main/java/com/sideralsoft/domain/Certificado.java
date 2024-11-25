package com.sideralsoft.domain;

import com.sideralsoft.domain.model.Elemento;

public class Certificado implements Actualizable {

    private Elemento elemento;
    private String ruta;

    public Certificado(Elemento elemento, String ruta) {
        this.elemento = elemento;
        this.ruta = ruta;
    }

    @Override
    public void actualizar() {
        this.detenerProcesos();
        this.reemplazarElementos();
        this.borrarArchivosTemporales();
        this.iniciarProcesos();
    }

    @Override
    public void detenerProcesos() {

    }

    @Override
    public void reemplazarElementos() {

    }

    @Override
    public void borrarArchivosTemporales() {

    }

    @Override
    public void iniciarProcesos() {

    }
}

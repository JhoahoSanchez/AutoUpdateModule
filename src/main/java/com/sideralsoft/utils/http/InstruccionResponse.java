package com.sideralsoft.utils.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InstruccionResponse {

    @JsonProperty("elemento")
    private String elemento;
    @JsonProperty("ruta")
    private String ruta;
    @JsonProperty("accion")
    private TipoAccion accion;

    public String getElemento() {
        return elemento;
    }

    public void setElemento(String elemento) {
        this.elemento = elemento;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public TipoAccion getAccion() {
        return accion;
    }

    public void setAccion(TipoAccion accion) {
        this.accion = accion;
    }

    @Override
    public String toString() {
        return "InstruccionResponse{" +
                "elemento='" + elemento + '\'' +
                ", ruta='" + ruta + '\'' +
                ", accion=" + accion +
                '}';
    }
}

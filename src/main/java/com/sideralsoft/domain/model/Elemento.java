package com.sideralsoft.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Elemento {

    @JsonProperty("nombreAplicacion")
    private String nombre;
    @JsonProperty("version")
    private String version;
    @JsonProperty("hash")
    private String hash;
    @JsonProperty("ubicacion")
    private String ubicacion;
    private TipoElemento tipo;

    public Elemento() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public TipoElemento getTipo() {
        return tipo;
    }

    public void setTipo(TipoElemento tipo) {
        this.tipo = tipo;
    }
}

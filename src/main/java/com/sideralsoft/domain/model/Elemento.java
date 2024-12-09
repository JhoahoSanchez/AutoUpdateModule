package com.sideralsoft.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Elemento {

    @JsonProperty("nombre")
    private String nombre;
    @JsonProperty("version")
    private String version;
    @JsonProperty("hash")
    private String hash;
    @JsonProperty("ruta")
    private String ruta;
    @JsonProperty("tipo")
    private TipoElemento tipo;
    @JsonProperty("proceso")
    private String proceso;
    @JsonProperty("rutaProceso")
    private String rutaProceso;

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

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public TipoElemento getTipo() {
        return tipo;
    }

    public void setTipo(TipoElemento tipo) {
        this.tipo = tipo;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getRutaProceso() {
        return rutaProceso;
    }

    public void setRutaProceso(String rutaProceso) {
        this.rutaProceso = rutaProceso;
    }

    @Override
    public String toString() {
        return "Elemento{" +
                "nombre='" + nombre + '\'' +
                ", version='" + version + '\'' +
                ", hash='" + hash + '\'' +
                ", ruta='" + ruta + '\'' +
                ", tipo=" + tipo +
                '}';
    }
}

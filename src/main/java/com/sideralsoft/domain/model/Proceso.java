package com.sideralsoft.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Proceso {

    @JsonProperty("nombre")
    private String nombre;
    @JsonProperty("ruta")
    private String ruta;
    @JsonProperty("tipo")
    private TipoProceso tipo;

    public Proceso() {
    }

    public Proceso(String nombre, String ruta, TipoProceso tipo) {
        this.nombre = nombre;
        this.ruta = ruta;
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public TipoProceso getTipo() {
        return tipo;
    }

    public void setTipo(TipoProceso tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Proceso{" +
                "nombre='" + nombre + '\'' +
                ", ruta='" + ruta + '\'' +
                ", tipo=" + tipo +
                '}';
    }
}

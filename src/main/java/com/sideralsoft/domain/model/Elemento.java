package com.sideralsoft.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Elemento {

    @JsonProperty("nombre")
    private String nombre;
    @JsonProperty("version")
    private String version;
    @JsonProperty("ruta")
    private String ruta;
    @JsonProperty("tipo")
    private TipoElemento tipo;
    @JsonProperty("proceso")
    private List<Proceso> procesos;
    @JsonProperty("dependencias")
    private List<Dependencia> dependencias;

    public Elemento() {
    }

    public Elemento(String nombre, String version) {
        this.nombre = nombre;
        this.version = version;
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

    public List<Proceso> getProcesos() {
        return procesos;
    }

    public void setProcesos(List<Proceso> procesos) {
        this.procesos = procesos;
    }

    public List<Dependencia> getDependencias() {
        return dependencias;
    }

    public void setDependencias(List<Dependencia> dependencias) {
        this.dependencias = dependencias;
    }

    @Override
    public String toString() {
        return "Elemento{" +
                "nombre='" + nombre + '\'' +
                ", version='" + version + '\'' +
                ", ruta='" + ruta + '\'' +
                ", tipo=" + tipo +
                ", procesos=" + procesos +
                ", dependencias=" + dependencias +
                '}';
    }
}

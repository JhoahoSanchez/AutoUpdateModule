package com.sideralsoft.utils.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActualizacionRequest {

    @JsonProperty("nombre")
    private String nombre;
    @JsonProperty("version")
    private String version;
    @JsonProperty("instrucciones")
    private List<InstruccionResponse> instrucciones;

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

    public List<InstruccionResponse> getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(List<InstruccionResponse> instrucciones) {
        this.instrucciones = instrucciones;
    }
}

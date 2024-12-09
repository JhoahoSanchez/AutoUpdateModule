package com.sideralsoft.utils.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InstruccionResponse {

    @JsonProperty("elemento")
    private String elemento;
    @JsonProperty("rutaInstalacion")
    private String rutaInstalacion;
    @JsonProperty("rutaAPI")
    private String rutaAPI; //TODO: Para amazon S3, se debe borrar
    @JsonProperty("hash")
    private String hash;
    @JsonProperty("accion")
    private TipoAccion accion;

    public String getElemento() {
        return elemento;
    }

    public void setElemento(String elemento) {
        this.elemento = elemento;
    }

    public String getRutaInstalacion() {
        return rutaInstalacion;
    }

    public void setRutaInstalacion(String rutaInstalacion) {
        this.rutaInstalacion = rutaInstalacion;
    }

    public String getRutaAPI() {
        return rutaAPI;
    }

    public void setRutaAPI(String rutaAPI) {
        this.rutaAPI = rutaAPI;
    }

    public TipoAccion getAccion() {
        return accion;
    }

    public void setAccion(TipoAccion accion) {
        this.accion = accion;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "InstruccionResponse{" +
                "elemento='" + elemento + '\'' +
                ", rutaInstalacion='" + rutaInstalacion + '\'' +
                ", rutaAPI='" + rutaAPI + '\'' +
                ", hash='" + hash + '\'' +
                ", accion=" + accion +
                '}';
    }
}

package com.sideralsoft.utils.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActualizacionResponse {

    @JsonProperty("mensaje")
    private String mensaje;
    @JsonProperty("actualizable")
    private boolean actualizable;
    @JsonProperty("version")
    private String version;

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isActualizable() {
        return actualizable;
    }

    public void setActualizable(boolean actualizable) {
        this.actualizable = actualizable;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "ActualizacionResponse{" +
                "mensaje='" + mensaje + '\'' +
                ", actualizable=" + actualizable +
                ", version='" + version + '\'' +
                '}';
    }
}

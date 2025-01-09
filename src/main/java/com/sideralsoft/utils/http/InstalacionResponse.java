package com.sideralsoft.utils.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sideralsoft.domain.model.Proceso;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InstalacionResponse {

    @JsonProperty("mensaje")
    private String mensaje;
    @JsonProperty("actualizable")
    private boolean actualizable;
    @JsonProperty("version")
    private String version;
    @JsonProperty("procesos")
    private List<Proceso> procesos;

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

    public List<Proceso> getProcesos() {
        return procesos;
    }

    public void setProcesos(List<Proceso> procesos) {
        this.procesos = procesos;
    }
}

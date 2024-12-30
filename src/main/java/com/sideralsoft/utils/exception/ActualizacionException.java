package com.sideralsoft.utils.exception;

import java.io.Serial;

public class ActualizacionException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public ActualizacionException(String mensaje) {
        super(mensaje);
    }

    public ActualizacionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

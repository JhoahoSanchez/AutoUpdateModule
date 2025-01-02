package com.sideralsoft.utils.exception;

import java.io.Serial;

public class InstalacionException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public InstalacionException(String message) {
        super(message);
    }

    public InstalacionException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.bandsync.exception;

import org.springframework.http.HttpStatus;

public class CredencialesInvalidasException extends BandSyncException {

    public CredencialesInvalidasException() {
        super(
            "Correo o contraseña incorrectos",
            HttpStatus.UNAUTHORIZED,
            "CREDENCIALES_INVALIDAS"
        );
    }
}

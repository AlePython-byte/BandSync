package com.bandsync.exception;

import org.springframework.http.HttpStatus;

public class CorreoYaRegistradoException extends BandSyncException {

    public CorreoYaRegistradoException(String correo) {
        super(
            "El correo " + correo + " ya está registrado",
            HttpStatus.CONFLICT,
            "CORREO_DUPLICADO"
        );
    }
}

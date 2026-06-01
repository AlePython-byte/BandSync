package com.bandsync.exception;

import org.springframework.http.HttpStatus;

public class OperacionNoPermitidaException extends BandSyncException {

    public OperacionNoPermitidaException(String message) {
        super(
            message,
            HttpStatus.FORBIDDEN,
            "OPERACION_NO_PERMITIDA"
        );
    }
}

package com.bandsync.exception;

import org.springframework.http.HttpStatus;

public class BandaYaExisteException extends BandSyncException {

    public BandaYaExisteException(String nombre, String ciudad) {
        super(
            "Ya existe una banda con el nombre " + nombre + " en " + ciudad,
            HttpStatus.CONFLICT,
            "BANDA_DUPLICADA"
        );
    }
}

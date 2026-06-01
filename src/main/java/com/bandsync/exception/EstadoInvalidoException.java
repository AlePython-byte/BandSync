package com.bandsync.exception;

import org.springframework.http.HttpStatus;

public class EstadoInvalidoException extends BandSyncException {

    public EstadoInvalidoException(String entidad, String estadoActual, String estadoDeseado) {
        super(
            "No se puede cambiar " + entidad + " de " + estadoActual + " a " + estadoDeseado,
            HttpStatus.BAD_REQUEST,
            "TRANSICION_ESTADO_INVALIDA"
        );
    }
}

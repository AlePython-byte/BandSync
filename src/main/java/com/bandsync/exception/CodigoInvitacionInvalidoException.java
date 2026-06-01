package com.bandsync.exception;

import org.springframework.http.HttpStatus;

public class CodigoInvitacionInvalidoException extends BandSyncException {

    public CodigoInvitacionInvalidoException(String codigo) {
        super(
            "El código de invitación " + codigo + " no es válido o expiró",
            HttpStatus.BAD_REQUEST,
            "CODIGO_INVITACION_INVALIDO"
        );
    }
}

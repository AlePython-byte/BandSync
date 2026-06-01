package com.bandsync.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ConflictoExclusividadException extends BandSyncException {

    public ConflictoExclusividadException(String marca, List<String> exclusividadesConflicto) {
        super(
            "Conflicto de exclusividad con la marca " + marca + " en: " + exclusividadesConflicto,
            HttpStatus.CONFLICT,
            "CONFLICTO_EXCLUSIVIDAD"
        );
    }
}

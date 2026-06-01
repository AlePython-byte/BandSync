package com.bandsync.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BandSyncException {

    public ResourceNotFoundException(String resource, String id) {
        super(
            "Recurso " + resource + " con id " + id + " no encontrado",
            HttpStatus.NOT_FOUND,
            "RESOURCE_NOT_FOUND"
        );
    }
}

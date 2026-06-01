package com.bandsync.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BandSyncException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public BandSyncException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public BandSyncException(String message, Throwable cause, HttpStatus status, String errorCode) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }
}

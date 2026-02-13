package com.picman.picman.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Responds to an unauthorized request to an endpoint or to content.
 * Mapped with 403 HTTP status.
 * @see com.picman.picman.Endpoints.CentralizedHttpStatusHandler
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidEntryPointException extends RuntimeException {
    public InvalidEntryPointException(String msg) {
        super(msg);
    }
}

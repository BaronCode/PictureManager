package com.picman.picman.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Responds to a function yet to be implemented.<br>
 * Mostly used for debug purposes.<br>
 * Mapped with 501 HTTP status.<br>
 * @see com.picman.picman.Endpoints.CentralizedHttpStatusHandler
 */
@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
public class NotImplementedException extends RuntimeException {
    public NotImplementedException(String msg) {
        super(msg);
    }
}

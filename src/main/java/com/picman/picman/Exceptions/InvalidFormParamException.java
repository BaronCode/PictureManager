package com.picman.picman.Exceptions;

import java.io.InvalidObjectException;

/**
 * Responds to a form parameter having an illegal value.<br>
 * This exception could fire if a form input passes all previous checks.<br>
 * Mapped with 422 HTTP status.<br>
 * Added class of origin for message completeness.
 * @see com.picman.picman.Endpoints.CentralizedHttpStatusHandler
 */
public class InvalidFormParamException extends InvalidObjectException {
    public InvalidFormParamException(String message) {
        super(message);
    }
}

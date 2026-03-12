package com.picman.picman.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.security.PrivilegedActionException;

/**
 * Responds to a configuration value in picmansettings.pman not being recognized by the program.
 * @see com.picman.picman.SpringSettings.PicmanSettings
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidActionException extends UnsupportedOperationException {
    @Getter
    private final Class<?> originClass;

    public InvalidActionException(Class<?> originClass, String message) {
        super(message);
        this.originClass = originClass;
    }


}
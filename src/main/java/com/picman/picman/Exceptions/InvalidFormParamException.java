package com.picman.picman.Exceptions;

import lombok.Getter;

import java.io.InvalidObjectException;

public class InvalidFormParamException extends InvalidObjectException {
    @Getter
    private final Class<?> originClass;

    public InvalidFormParamException(Class<?> originClass, String message) {
        super(message);
        this.originClass = originClass;
    }
}

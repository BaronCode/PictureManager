package com.picman.picman.Exceptions;

import lombok.Getter;

public class EntityNotFoundException extends jakarta.persistence.EntityNotFoundException {
    @Getter
    private final Class<?> originClass;

    public EntityNotFoundException(Class<?> originClass, String message) {
        super(message);
        this.originClass = originClass;
    }


}

package com.picman.picman.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Responds to an Entity not being found at DataBase interrogation.
 * Mapped with a 404 HTTP status.
 * Added class of origin for message completeness.
 * @see com.picman.picman.Endpoints.CentralizedHttpStatusHandler
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends jakarta.persistence.EntityNotFoundException {
    @Getter
    private final Class<?> originClass;

    public EntityNotFoundException(Class<?> originClass, String message) {
        super(message);
        this.originClass = originClass;
    }


}

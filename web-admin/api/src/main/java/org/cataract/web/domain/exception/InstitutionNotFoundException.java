package org.cataract.web.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InstitutionNotFoundException extends RuntimeException {
    public InstitutionNotFoundException(String message) {
        super(message);
    }

    public InstitutionNotFoundException() {
        super("Institution does not exist");
    }
}

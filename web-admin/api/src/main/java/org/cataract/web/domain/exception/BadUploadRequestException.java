package org.cataract.web.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "bad upload request")
public class BadUploadRequestException extends RuntimeException {
    public BadUploadRequestException(String message) {
        super(message);
    }

    public BadUploadRequestException() {
        super("report should include at least one eye image");
    }
}
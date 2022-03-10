package nl.hetckm.bouncer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoFileAssociatedWithMediaException extends RuntimeException {
    public NoFileAssociatedWithMediaException(String message) {
        super(message);
    }
}

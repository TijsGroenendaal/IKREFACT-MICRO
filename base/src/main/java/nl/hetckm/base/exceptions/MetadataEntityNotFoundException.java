package nl.hetckm.base.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MetadataEntityNotFoundException extends RuntimeException{
    public MetadataEntityNotFoundException(String message) {
        super(message);
    }
}

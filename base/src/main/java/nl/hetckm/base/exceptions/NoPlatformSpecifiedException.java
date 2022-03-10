package nl.hetckm.base.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoPlatformSpecifiedException extends RuntimeException {
    public NoPlatformSpecifiedException() {
        super("No platform was specified.");
    }
}

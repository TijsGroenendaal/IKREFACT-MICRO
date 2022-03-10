package nl.hetckm.bouncer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AppUserDisabledException extends RuntimeException{

    public AppUserDisabledException() {
        super("user is disabled");
    }
}

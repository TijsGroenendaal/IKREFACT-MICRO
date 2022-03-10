package nl.hetckm.bouncer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class VerdictAlreadyExists extends RuntimeException{
    public VerdictAlreadyExists() {
        super("This challenge has already been reviewed.");
    }
}

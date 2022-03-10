package nl.hetckm.base.exceptions;

import nl.hetckm.base.model.Verification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
public class VerificationLifetimeReachedException extends RuntimeException{
    public VerificationLifetimeReachedException(Verification verification) {
        super("The maximum amount of challenges for '" + verification.getName() + "' has been reached");
    }
}

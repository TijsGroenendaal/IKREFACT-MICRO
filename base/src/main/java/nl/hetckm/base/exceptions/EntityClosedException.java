package nl.hetckm.base.exceptions;

import nl.hetckm.base.interfaces.Reviewable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
public class EntityClosedException extends RuntimeException{
    public <T> EntityClosedException(Class<T> entity, Reviewable verification) {
        super(entity.getSimpleName() +  " '" + verification.getName() + "' has been closed");
    }
}

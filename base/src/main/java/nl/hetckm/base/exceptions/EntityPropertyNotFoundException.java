package nl.hetckm.base.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EntityPropertyNotFoundException extends RuntimeException {
    public <T> EntityPropertyNotFoundException(Class<T> entity, String property) {
        super("Property '" + property + "' does not exist on type '" + entity.getSimpleName() + "'.");
    }
}

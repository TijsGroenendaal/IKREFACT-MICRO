package nl.hetckm.base.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
public class NotAllChallengesReviewedException extends RuntimeException {
    public NotAllChallengesReviewedException() {
        super("Not all challenges have been reviewed");
    }
}

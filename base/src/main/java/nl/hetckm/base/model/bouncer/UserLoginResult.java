package nl.hetckm.base.model.bouncer;

import lombok.Data;
import lombok.NonNull;

@Data
public class UserLoginResult {

    @NonNull
    private String token;
    @NonNull
    private UserResponse user;

}

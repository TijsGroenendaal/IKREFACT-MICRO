package nl.hetckm.bouncer.auth.model;

import lombok.Data;
import lombok.NonNull;
import nl.hetckm.bouncer.user.model.UserResponse;

@Data
public class UserLoginResult {

    @NonNull
    private String token;
    @NonNull
    private UserResponse user;

}

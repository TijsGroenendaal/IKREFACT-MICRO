package nl.hetckm.base.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class UserLoginResult {

    @NonNull
    private String token;
    @NonNull
    private UserResponse user;

}

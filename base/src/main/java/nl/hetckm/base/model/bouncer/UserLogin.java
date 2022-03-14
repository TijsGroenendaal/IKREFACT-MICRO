package nl.hetckm.base.model.bouncer;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserLogin {

    @NotEmpty
    String password;
    @NotEmpty
    String username;

}

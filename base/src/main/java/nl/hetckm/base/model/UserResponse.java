package nl.hetckm.base.model;

import lombok.Value;
import nl.hetckm.base.enums.Role;

import java.util.UUID;

@Value
public class UserResponse {

    UUID id;
    String username;
    Role role;
    boolean enabled;

    public UserResponse(AppUser appUser) {
        this.id = appUser.getId();
        this.username = appUser.getUsername();
        this.role = appUser.getRole();
        this.enabled = appUser.isEnabled();
    }

    public UserResponse(Role role, String username) {
        this.role = role;
        this.username = username;
        this.id = null;
        this.enabled = true;
    }

}

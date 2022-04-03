package nl.hetckm.base.helper;

import nl.hetckm.base.enums.Role;
import nl.hetckm.base.exceptions.EntityNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.UUID;

public class RelationHelper {

    public static <T> void isFromParent(UUID childParentId, UUID parentId, Class<T> child) {
        if (!childParentId.equals(parentId)) throw new EntityNotFoundException(child);
    }

    public static UUID getPlatformId() {
        List<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().
                getAuthorities().stream().toList();
        try {
            return UUID.fromString(authorities.get(0).toString());
        } catch (IllegalArgumentException e) {
            if (authorities.size() != 1) {
                return UUID.fromString(authorities.get(1).toString());
            } else {
                return null;
            }
        }
    }

    public static Role getRoleFromJWT(Jwt jwt, String AUTHORITIES_CLAIM_NAME) {
        final String[] authorities = jwt.getClaims().get(AUTHORITIES_CLAIM_NAME).toString().split(" ");
        try {
            return Role.valueOf(authorities[0]);
        } catch (IllegalArgumentException e) {
            return Role.valueOf(authorities[1]);
        }
    }

    public static UUID getPlatformIdFromJWT(Jwt jwt, String AUTHORITIES_CLAIM_NAME) {
        final String[] authorities = jwt.getClaims().get(AUTHORITIES_CLAIM_NAME).toString().split(" ");
        try {
            return UUID.fromString(authorities[0]);
        } catch (IllegalArgumentException e) {
            if (authorities.length > 1) {
                return UUID.fromString(authorities[1]);
            } else {
                return null;
            }
        }
    }
}

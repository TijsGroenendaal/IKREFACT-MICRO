package nl.hetckm.bouncer.helper;

import nl.hetckm.bouncer.exceptions.EntityNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

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

}

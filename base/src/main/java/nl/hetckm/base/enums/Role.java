package nl.hetckm.base.enums;

public enum Role {
    SUPERUSER("superuser"),
    ADMIN("admin"),
    MODERATOR("moderator"),
    PLATFORM("platform");

    public final String value;

    Role(String value) {
        this.value = value;
    }
}

package nl.hetckm.base.model;

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

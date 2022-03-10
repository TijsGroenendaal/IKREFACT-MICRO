package nl.hetckm.bouncer.verification.model;

public enum VerificationStatus {
    REJECTED("rejected"),
    ACCEPTED("accepted"),
    OPEN("open");

    public final String value;

    VerificationStatus(String value) {
        this.value = value;
    }
}

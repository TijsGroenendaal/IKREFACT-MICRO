package nl.hetckm.bouncer.challenge.model;

public enum ChallengeStatus {
    REJECTED("rejected"),
    ACCEPTED("accepted"),
    OPEN("open");

    public final String value;

    ChallengeStatus(String value) {
        this.value = value;
    }
}

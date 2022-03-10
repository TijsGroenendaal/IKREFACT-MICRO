package nl.hetckm.bouncer.challenge.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class NewChallengeRequest {
    UUID presetId;
}

package nl.hetckm.bouncer.verdict.model;

import lombok.Data;

@Data
public class VerdictAddModel {
    private boolean accepted;
    private String reason;
}

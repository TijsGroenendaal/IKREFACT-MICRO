package nl.hetckm.base.model.bouncer;

import lombok.Data;

@Data
public class VerdictAddModel {
    private boolean accepted;
    private String reason;
}

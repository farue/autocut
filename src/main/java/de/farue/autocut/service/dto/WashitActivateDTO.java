package de.farue.autocut.service.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WashitActivateDTO {

    private int machineId;
    private Instant activationTimestamp;
    private Instant endActivationTime;
    private long activationDurationMs;
}

package de.farue.autocut.service.dto;

import java.time.Instant;
import lombok.Data;

@Data
public class NetworkStatusDTO {

    private Long networkSwitchId;
    private String port;
    private String status;
    private int speed;
    private int maxPossibleSpeed;
    private Instant lastUpdate;
}

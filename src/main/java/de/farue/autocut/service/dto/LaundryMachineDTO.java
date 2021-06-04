package de.farue.autocut.service.dto;

import de.farue.autocut.domain.enumeration.LaundryMachineType;
import java.time.Instant;
import lombok.Data;

@Data
public class LaundryMachineDTO {

    private Long id;
    private String name;
    private LaundryMachineType type;
    private Boolean enabled;
    private Integer positionX;
    private Integer positionY;
    private Instant inUseUntil;
}

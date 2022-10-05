package de.farue.autocut.service.dto;

import java.time.Instant;
import lombok.Data;

@Data
public class TimeRangeDTO {

    private Instant earliest;
    private Instant latest;
}

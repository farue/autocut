package de.farue.autocut.service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTimesheetTimeDTO {

    @NotNull
    private Instant start;

    @NotNull
    private Instant end;

    @NotNull
    private Integer pause;

    @NotNull
    @Size(max = 1000)
    private String description;

    private BigDecimal editedFactor;
    private Integer editedConstant;

    @NotNull
    private Long projectId;

    @NotNull
    private Long taskId;

    private boolean stopTimer;
}

package de.farue.autocut.service.dto;

import de.farue.autocut.domain.Timesheet;
import de.farue.autocut.domain.TimesheetProject;
import de.farue.autocut.domain.TimesheetTask;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;

@Data
public class TimesheetTimeDTO {

    private Long id;
    private Instant start;
    private Instant end;
    private Integer effectiveTime;
    private Integer pause;
    private String description;
    private BigDecimal editedFactor;
    private Integer editedConstant;
    private Timesheet timesheet;
    private TimesheetProject project;
    private TimesheetTask task;
    private boolean editable;
}

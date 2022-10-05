package de.farue.autocut.domain;

import lombok.Data;

@Data
public class TimesheetStatement {

    private long timesheetId;
    private long workedTime;
}

package de.farue.autocut.service;

import de.farue.autocut.utils.DateUtil;
import java.time.Duration;

public class TimesheetTimeEntryTooOldException extends RuntimeException {

    private final ChangeType change;
    private final Duration permittedDuration;

    public TimesheetTimeEntryTooOldException(ChangeType change, Duration permittedDuration) {
        super();
        this.change = change;
        this.permittedDuration = permittedDuration;
    }

    public ChangeType getChange() {
        return change;
    }

    public Duration getPermittedDuration() {
        return permittedDuration;
    }

    @Override
    public String getMessage() {
        return "Entry cannot be " + mapChange(change) + " after " + DateUtil.format(permittedDuration) + ".";
    }

    private String mapChange(ChangeType change) {
        return switch (change) {
            case CREATE -> "created";
            case UPDATE -> "updated";
            case DELETE -> "deleted";
        };
    }
}

package de.farue.autocut.web.rest.errors;

import de.farue.autocut.service.TimesheetTimeService;
import java.time.temporal.ChronoUnit;

public class TimesheetTimeEntryTooOldException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public TimesheetTimeEntryTooOldException() {
        super(
            ErrorConstants.TIMESHEET_TIME_ENTRY_TOO_OLD,
            "Times must be booked within " + TimesheetTimeService.BOOKING_PERIOD.get(ChronoUnit.SECONDS) / 3600 + " hours",
            "timesheetTime",
            "timesheettimeentrytooold"
        );
    }
}

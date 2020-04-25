package de.farue.autocut.service;

import de.farue.autocut.web.rest.errors.BadRequestAlertException;
import de.farue.autocut.web.rest.errors.ErrorConstants;

public class LaundryMachineUnavailableException extends BadRequestAlertException {

    public LaundryMachineUnavailableException() {
        super(ErrorConstants.LAUNDRY_MACHINE_UNAVAILABLE_TYPE, "Laundry machine is currently not available", "washing",
            "machinenotavailable");
    }
}

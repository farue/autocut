package de.farue.autocut.service;

import de.farue.autocut.web.rest.errors.BadRequestAlertException;
import de.farue.autocut.web.rest.errors.ErrorConstants;

public class LaundryMachineDoesNotExistException extends BadRequestAlertException {

    public LaundryMachineDoesNotExistException() {
        super(ErrorConstants.LAUNDRY_MACHINE_DOES_NOT_EXIST_TYPE, "Laundry machine does not exist", "washing",
            "machinenotexist");
    }
}

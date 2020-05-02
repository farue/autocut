package de.farue.autocut.web.rest.errors;

public class LaundryMachineDoesNotExistException extends BadRequestAlertException {

    public LaundryMachineDoesNotExistException() {
        super(ErrorConstants.LAUNDRY_MACHINE_DOES_NOT_EXIST_TYPE, "Laundry machine does not exist", "washing",
            "machinenotexist");
    }
}

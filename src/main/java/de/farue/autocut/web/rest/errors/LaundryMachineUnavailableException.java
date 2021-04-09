package de.farue.autocut.web.rest.errors;

public class LaundryMachineUnavailableException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public LaundryMachineUnavailableException() {
        super(
            ErrorConstants.LAUNDRY_MACHINE_UNAVAILABLE_TYPE,
            "Laundry machine is currently not available",
            "washing",
            "machinenotavailable"
        );
    }
}

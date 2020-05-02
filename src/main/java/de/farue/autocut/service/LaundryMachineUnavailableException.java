package de.farue.autocut.service;

public class LaundryMachineUnavailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LaundryMachineUnavailableException() {
        super("Laundry machine unavailable");
    }
}

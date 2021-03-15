package de.farue.autocut.service.internetaccess;

public class SwitchCommandExecutionException extends RuntimeException {

    public SwitchCommandExecutionException() {
        super();
    }

    public SwitchCommandExecutionException(String message) {
        super(message);
    }

    public SwitchCommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SwitchCommandExecutionException(Throwable cause) {
        super(cause);
    }
}

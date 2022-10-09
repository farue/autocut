package de.farue.autocut.web.rest.errors;

public class ValidationException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public ValidationException() {
        super(ErrorConstants.INVALID_PAYLOAD, "Payload validation failed!", "", "invalidpayload");
    }
}

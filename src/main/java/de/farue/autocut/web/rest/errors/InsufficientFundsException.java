package de.farue.autocut.web.rest.errors;

public class InsufficientFundsException extends BadRequestAlertException {

    public InsufficientFundsException() {
        super(ErrorConstants.INSUFFICIENT_FUNDS_TYPE, "Insufficient funds", "lease",
            "insufficientfunds");
    }
}

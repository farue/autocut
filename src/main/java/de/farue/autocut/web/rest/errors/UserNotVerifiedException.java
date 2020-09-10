package de.farue.autocut.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class UserNotVerifiedException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public UserNotVerifiedException() {
        super(ErrorConstants.USER_NOT_VERIFIED_TYPE, "User not verified", Status.UNAUTHORIZED, null, null, null, null);
    }
}

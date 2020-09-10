package de.farue.autocut.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class UserNotActivatedException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public UserNotActivatedException() {
        super(ErrorConstants.USER_NOT_ACTIVATED_TYPE, "User not activated", Status.UNAUTHORIZED, null, null, null, null);
    }
}

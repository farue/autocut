package de.farue.autocut.security;

import org.springframework.security.core.AuthenticationException;

/**
 * This exception is thrown in case of a not verified user trying to authenticate.
 */
public class UserNotVerifiedException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public UserNotVerifiedException(String message) {
        super(message);
    }

    public UserNotVerifiedException(String message, Throwable t) {
        super(message, t);
    }
}

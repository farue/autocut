package de.farue.autocut.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";
    public static final String VIEW_TRANSACTIONS = "VIEW_TRANSACTIONS";
    public static final String EDIT_TRANSACTIONS = "EDIT_TRANSACTIONS";
    public static final String[] SYSTEM = { ADMIN, VIEW_TRANSACTIONS, EDIT_TRANSACTIONS };

    private AuthoritiesConstants() {}
}

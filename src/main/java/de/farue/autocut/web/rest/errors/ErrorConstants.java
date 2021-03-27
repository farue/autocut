package de.farue.autocut.web.rest.errors;

import java.net.URI;

public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String PROBLEM_BASE_URL = "https://www.jhipster.tech/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
    public static final URI INVALID_PASSWORD_TYPE = URI.create(PROBLEM_BASE_URL + "/invalid-password");
    public static final URI EMAIL_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/email-already-used");
    public static final URI LOGIN_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/login-already-used");
    public static final URI USER_NOT_ACTIVATED_TYPE = URI.create(PROBLEM_BASE_URL + "/user-not-activated");
    public static final URI USER_NOT_VERIFIED_TYPE = URI.create(PROBLEM_BASE_URL + "/user-not-verified");

    // Washing
    public static final URI INSUFFICIENT_FUNDS_TYPE = URI.create(PROBLEM_BASE_URL + "/insufficient-funds");
    public static final URI LAUNDRY_MACHINE_UNAVAILABLE_TYPE = URI.create(PROBLEM_BASE_URL + "/laundry-machine-unavailable");
    public static final URI LAUNDRY_MACHINE_DOES_NOT_EXIST_TYPE = URI.create(PROBLEM_BASE_URL + "/laundry-machine-does-not-exist");

    private ErrorConstants() {}
}

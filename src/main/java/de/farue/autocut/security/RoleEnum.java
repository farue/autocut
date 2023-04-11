package de.farue.autocut.security;

public enum RoleEnum {
    EMPTY(),
    SYSTEM(AuthoritiesConstants.ADMIN, AuthoritiesConstants.VIEW_TRANSACTIONS, AuthoritiesConstants.EDIT_TRANSACTIONS);

    private String[] authorities;

    RoleEnum(String... authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }
}

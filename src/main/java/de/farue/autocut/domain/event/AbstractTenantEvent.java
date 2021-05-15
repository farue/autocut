package de.farue.autocut.domain.event;

import de.farue.autocut.domain.Tenant;

public abstract class AbstractTenantEvent {

    private final Tenant tenant;

    protected AbstractTenantEvent(Tenant tenant) {
        this.tenant = tenant;
    }

    public Tenant getTenant() {
        return tenant;
    }
}

package de.farue.autocut.domain.event;

import de.farue.autocut.domain.Tenant;

public class TenantVerifiedEvent extends AbstractTenantEvent {

    public TenantVerifiedEvent(Tenant tenant) {
        super(tenant);
    }
}

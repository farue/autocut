package de.farue.autocut.domain.event;

import de.farue.autocut.domain.Tenant;

public class TenantCreatedEvent extends AbstractTenantEvent {

    public TenantCreatedEvent(Tenant tenant) {
        super(tenant);
    }
}

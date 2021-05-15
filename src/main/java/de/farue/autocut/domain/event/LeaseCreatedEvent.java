package de.farue.autocut.domain.event;

import de.farue.autocut.domain.Lease;

public class LeaseCreatedEvent extends AbstractLeaseEvent {

    public LeaseCreatedEvent(Lease lease) {
        super(lease);
    }
}

package de.farue.autocut.domain.event;

import de.farue.autocut.domain.Lease;

public class LeaseUpdatedEvent extends AbstractLeaseEvent {

    public LeaseUpdatedEvent(Lease lease) {
        super(lease);
    }
}

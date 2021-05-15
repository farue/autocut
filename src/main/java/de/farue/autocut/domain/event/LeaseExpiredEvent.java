package de.farue.autocut.domain.event;

import de.farue.autocut.domain.Lease;

public class LeaseExpiredEvent extends AbstractLeaseEvent {

    public LeaseExpiredEvent(Lease lease) {
        super(lease);
    }
}

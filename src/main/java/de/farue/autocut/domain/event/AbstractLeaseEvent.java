package de.farue.autocut.domain.event;

import de.farue.autocut.domain.Lease;

public abstract class AbstractLeaseEvent {

    private final Lease lease;

    public AbstractLeaseEvent(Lease lease) {
        this.lease = lease;
    }

    public Lease getLease() {
        return lease;
    }
}

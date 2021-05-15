package de.farue.autocut.domain.event;

import de.farue.autocut.domain.InternalTransaction;

public class InternalTransactionCreatedEvent {

    private final InternalTransaction transaction;

    public InternalTransactionCreatedEvent(InternalTransaction transaction) {
        this.transaction = transaction;
    }

    public InternalTransaction getTransaction() {
        return transaction;
    }
}

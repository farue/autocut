package de.farue.autocut.domain.event;

import de.farue.autocut.domain.InternalTransaction;

public class InternalTransactionEffectiveEvent {

    private final InternalTransaction transaction;

    public InternalTransactionEffectiveEvent(InternalTransaction transaction) {
        this.transaction = transaction;
    }

    public InternalTransaction getTransaction() {
        return transaction;
    }
}

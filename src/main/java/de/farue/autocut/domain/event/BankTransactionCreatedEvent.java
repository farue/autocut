package de.farue.autocut.domain.event;

import de.farue.autocut.domain.BankTransaction;

public class BankTransactionCreatedEvent {

    private final BankTransaction transaction;

    public BankTransactionCreatedEvent(BankTransaction transaction) {
        this.transaction = transaction;
    }

    public BankTransaction getTransaction() {
        return transaction;
    }
}

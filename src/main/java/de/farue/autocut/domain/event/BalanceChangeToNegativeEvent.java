package de.farue.autocut.domain.event;

import de.farue.autocut.domain.InternalTransaction;

public class BalanceChangeToNegativeEvent {

    private final InternalTransaction previousTransaction;
    private final InternalTransaction currentTransaction;

    public BalanceChangeToNegativeEvent(InternalTransaction previousTransaction, InternalTransaction currentTransaction) {
        this.previousTransaction = previousTransaction;
        this.currentTransaction = currentTransaction;
    }

    public InternalTransaction getPreviousTransaction() {
        return previousTransaction;
    }

    public InternalTransaction getCurrentTransaction() {
        return currentTransaction;
    }
}

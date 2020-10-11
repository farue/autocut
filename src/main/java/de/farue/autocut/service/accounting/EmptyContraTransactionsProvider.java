package de.farue.autocut.service.accounting;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import de.farue.autocut.domain.Transaction;

public class EmptyContraTransactionsProvider implements ContraTransactionsProvider {

    @Override
    public List<Transaction> calculateContraTransactions(List<Transaction> transactions,
        Instant bookingDate, Instant valueDate) {
        return new ArrayList<>();
    }
}

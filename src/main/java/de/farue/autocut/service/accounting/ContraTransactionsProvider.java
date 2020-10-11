package de.farue.autocut.service.accounting;

import java.time.Instant;
import java.util.List;

import de.farue.autocut.domain.Transaction;

public interface ContraTransactionsProvider {

    List<Transaction> calculateContraTransactions(List<Transaction> transactions, Instant bookingDate, Instant valueDate);
}

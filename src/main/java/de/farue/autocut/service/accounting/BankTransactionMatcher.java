package de.farue.autocut.service.accounting;

import de.farue.autocut.domain.BankTransaction;
import de.farue.autocut.domain.TransactionBook;
import java.util.Optional;

public interface BankTransactionMatcher {
    Optional<TransactionBook> findMatch(BankTransaction bankTransaction);
}

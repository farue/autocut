package de.farue.autocut.service.accounting;

import java.util.Optional;

import de.farue.autocut.domain.BankTransaction;
import de.farue.autocut.domain.TransactionBook;

public interface BankTransactionMatcher {

    Optional<TransactionBook> findMatch(BankTransaction bankTransaction);

}

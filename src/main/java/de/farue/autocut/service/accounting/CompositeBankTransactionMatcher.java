package de.farue.autocut.service.accounting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.farue.autocut.domain.BankTransaction;
import de.farue.autocut.domain.TransactionBook;

public class CompositeBankTransactionMatcher implements BankTransactionMatcher {

    private List<BankTransactionMatcher> matchers = new ArrayList<>();

    public List<BankTransactionMatcher> getMatchers() {
        return matchers;
    }

    public void setMatchers(List<BankTransactionMatcher> matchers) {
        this.matchers = matchers;
    }

    @Override
    public Optional<TransactionBook> findMatch(BankTransaction bankTransaction) {
        for (BankTransactionMatcher matcher : matchers) {
            Optional<TransactionBook> transactionBookOptional = matcher.findMatch(bankTransaction);
            if (transactionBookOptional.isPresent()) {
                return transactionBookOptional;
            }
        }
        return Optional.empty();
    }
}

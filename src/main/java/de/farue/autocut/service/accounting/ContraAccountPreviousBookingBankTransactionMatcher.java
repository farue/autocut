package de.farue.autocut.service.accounting;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.farue.autocut.domain.BankAccount;
import de.farue.autocut.domain.BankTransaction;
import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.repository.BankTransactionRepository;

public class ContraAccountPreviousBookingBankTransactionMatcher implements BankTransactionMatcher {

    private final BankTransactionRepository bankTransactionRepository;

    public ContraAccountPreviousBookingBankTransactionMatcher(BankTransactionRepository bankTransactionRepository) {
        this.bankTransactionRepository = bankTransactionRepository;
    }

    @Override
    public Optional<TransactionBook> findMatch(BankTransaction bankTransaction) {
        BankAccount contraBankAccount = bankTransaction.getContraBankAccount();
        if (contraBankAccount == null || contraBankAccount.getId() == null) {
            return Optional.empty();
        }

        Set<TransactionBook> linkedTransactionBooks = bankTransactionRepository.findAllByContraBankAccount(contraBankAccount)
            .stream()
            .flatMap(t -> t.getLefts().stream())
            .map(Transaction::getTransactionBook)
            .collect(Collectors.toSet());

        if (linkedTransactionBooks.size() == 1) {
            return Optional.ofNullable(linkedTransactionBooks.iterator().next());
        }

        return Optional.empty();
    }
}

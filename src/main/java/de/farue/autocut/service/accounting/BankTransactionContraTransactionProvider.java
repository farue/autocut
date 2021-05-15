package de.farue.autocut.service.accounting;

import static de.farue.autocut.utils.BigDecimalUtil.compare;

import de.farue.autocut.domain.BankTransaction;
import de.farue.autocut.domain.InternalTransaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionType;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

public class BankTransactionContraTransactionProvider {

    private final BankTransactionMatcher bankTransactionMatcher;

    private String issuer = "BankTransactionService";

    public BankTransactionContraTransactionProvider(BankTransactionMatcher bankTransactionMatcher) {
        this.bankTransactionMatcher = bankTransactionMatcher;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Optional<InternalTransaction> calculateContraTransaction(BankTransaction bankTransaction) {
        return bankTransactionMatcher
            .findMatch(bankTransaction)
            .map(contraTransactionBook -> createContraTransaction(bankTransaction, contraTransactionBook));
    }

    private InternalTransaction createContraTransaction(BankTransaction bankTransaction, TransactionBook transactionBook) {
        Instant timestamp = Instant.now();
        return new InternalTransaction()
            .bookingDate(timestamp)
            .valueDate(timestamp)
            .value(bankTransaction.getValue())
            .description(getDescription(bankTransaction))
            .issuer(issuer)
            .transactionType(getType(bankTransaction))
            .transactionBook(transactionBook);
    }

    private TransactionType getType(BankTransaction bankTransaction) {
        return compare(bankTransaction.getValue()).isNegative() ? TransactionType.DEBIT : TransactionType.CREDIT;
    }

    private String getDescription(BankTransaction bankTransaction) {
        return (
            "i18n{transaction.descriptions.bankTransfer} " + LocalDate.ofInstant(bankTransaction.getBookingDate(), ZoneId.systemDefault())
        );
    }
}

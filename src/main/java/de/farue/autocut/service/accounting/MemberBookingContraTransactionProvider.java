package de.farue.autocut.service.accounting;

import static de.farue.autocut.utils.BigDecimalUtil.compare;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionKind;

public class MemberBookingContraTransactionProvider implements ContraTransactionsProvider {

    private TransactionBook ownCashTransactionBook;
    private TransactionBook ownRevenueTransactionBook;

    public MemberBookingContraTransactionProvider(TransactionBook ownCashTransactionBook, TransactionBook ownRevenueTransactionBook) {
        this.ownCashTransactionBook = ownCashTransactionBook;
        this.ownRevenueTransactionBook = ownRevenueTransactionBook;
    }

    @Override
    public List<Transaction> calculateContraTransactions(List<Transaction> transactions,
        Instant bookingDate, Instant valueDate) {
        List<Transaction> contraTransactions = new ArrayList<>();
        Map<TransactionKind, BigDecimal> summedValueByTransactionKind = transactions.stream().collect(Collectors.groupingBy(Transaction::getKind,
            Collectors.mapping(Transaction::getValue, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
        for (Entry<TransactionKind, BigDecimal> entry : summedValueByTransactionKind.entrySet()) {
            TransactionKind transactionKind = entry.getKey();
            BigDecimal summedValue = entry.getValue();
            if (!compare(summedValue).isZero()) {
                Transaction contraTransaction = createPartialContraTransaction(summedValue, transactionKind)
                    .bookingDate(bookingDate)
                    .valueDate(valueDate);
                contraTransactions.add(contraTransaction);
            }
        }
        return contraTransactions;
    }

    private Transaction createPartialContraTransaction(BigDecimal value, TransactionKind transactionKind) {
        BigDecimal contraValue;
        TransactionKind contraType;
        TransactionBook targetTransactionBook;
        switch (transactionKind) {
            case FEE, PURCHASE -> {
                contraType = TransactionKind.CREDIT;
                contraValue = value.negate();
                targetTransactionBook = ownRevenueTransactionBook;
            }
            case DEBIT -> {
                contraType = TransactionKind.DEBIT;
                contraValue = value;
                targetTransactionBook = ownCashTransactionBook;
            }
            case CORRECTION -> {
                contraType = TransactionKind.CORRECTION;
                contraValue = value.negate();
                targetTransactionBook = ownRevenueTransactionBook;
            }
            case CREDIT -> throw new IllegalArgumentException("Credit bookings for members have to originate from transactions on own cash account");
            case TRANSFER -> throw new IllegalArgumentException("The sum of all transfer bookings must be 0.");
            default -> throw new IllegalStateException("Unexpected value: " + transactionKind);
        }

        return new Transaction()
            .kind(contraType)
            .value(contraValue)
            .issuer(TransactionBookService.class.getSimpleName())
            .transactionBook(targetTransactionBook);
    }
}

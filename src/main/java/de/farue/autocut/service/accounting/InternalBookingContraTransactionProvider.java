package de.farue.autocut.service.accounting;

import static de.farue.autocut.utils.BigDecimalUtil.compare;

import de.farue.autocut.domain.InternalTransaction;
import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionType;
import de.farue.autocut.service.AssociationService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class InternalBookingContraTransactionProvider {

    private final AssociationService associationService;

    public InternalBookingContraTransactionProvider(AssociationService associationService) {
        this.associationService = associationService;
    }

    public List<InternalTransaction> calculateContraTransactions(
        List<InternalTransaction> transactions,
        Instant bookingDate,
        Instant valueDate
    ) {
        List<InternalTransaction> contraTransactions = new ArrayList<>();
        Map<TransactionType, BigDecimal> summedValueByTransactionKind = transactions
            .stream()
            .collect(
                Collectors.groupingBy(
                    InternalTransaction::getTransactionType,
                    Collectors.mapping(Transaction::getValue, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                )
            );
        for (Entry<TransactionType, BigDecimal> entry : summedValueByTransactionKind.entrySet()) {
            TransactionType transactionType = entry.getKey();
            BigDecimal summedValue = entry.getValue();
            if (!compare(summedValue).isZero()) {
                InternalTransaction contraTransaction = createPartialContraTransaction(summedValue, transactionType)
                    .bookingDate(bookingDate)
                    .valueDate(valueDate);
                contraTransactions.add(contraTransaction);
            }
        }
        return contraTransactions;
    }

    private InternalTransaction createPartialContraTransaction(BigDecimal value, TransactionType type) {
        BigDecimal contraValue;
        TransactionType contraType;
        TransactionBook targetTransactionBook;
        switch (type) {
            case FEE, PURCHASE -> {
                contraType = TransactionType.CREDIT;
                contraValue = value.negate();
                targetTransactionBook = associationService.getRevenueTransactionBook();
            }
            case DEBIT -> {
                contraType = TransactionType.DEBIT;
                contraValue = value;
                targetTransactionBook = associationService.getCashTransactionBook();
            }
            case CORRECTION -> {
                contraType = TransactionType.CORRECTION;
                contraValue = value.negate();
                targetTransactionBook = associationService.getRevenueTransactionBook();
            }
            case CREDIT -> {
                contraType = TransactionType.DEBIT;
                contraValue = value.negate();
                targetTransactionBook = associationService.getRevenueTransactionBook();
            }
            case TRANSFER -> throw new IllegalArgumentException("The sum of all transfer bookings must be 0.");
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }

        return new InternalTransaction()
            .transactionType(contraType)
            .value(contraValue)
            .issuer(TransactionBookService.class.getSimpleName())
            .transactionBook(targetTransactionBook);
    }
}

package de.farue.autocut.service.accounting;

import static de.farue.autocut.utils.BigDecimalUtil.compare;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.domain.InternalTransaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionType;
import de.farue.autocut.repository.InternalTransactionRepository;
import de.farue.autocut.repository.TransactionRepository;
import de.farue.autocut.service.InsufficientFundsException;
import de.farue.autocut.service.TransactionService;

@Service
@Transactional
public class InternalTransactionService extends TransactionService<InternalTransaction> {

    private final InternalTransactionRepository transactionRepository;
    private final TransactionBookService transactionBookService;
    private final InternalBookingContraTransactionProvider internalBookingContraTransactionProvider;

    public InternalTransactionService(InternalTransactionRepository transactionRepository,
        TransactionBookService transactionBookService,
        InternalBookingContraTransactionProvider internalBookingContraTransactionProvider) {
        this.transactionRepository = transactionRepository;
        this.transactionBookService = transactionBookService;
        this.internalBookingContraTransactionProvider = internalBookingContraTransactionProvider;
    }

    @Override
    protected TransactionRepository<InternalTransaction> getRepository() {
        return transactionRepository;
    }

    @Override
    public InternalTransaction save(InternalTransaction transaction) {
        validate(transaction);
        return super.save(transaction);
    }

    public void saveWithContraTransaction(BookingTemplate bookingTemplate) {
        saveBooking(bookingTemplate, internalBookingContraTransactionProvider);
    }

    private void saveBooking(BookingTemplate bookingTemplate, InternalBookingContraTransactionProvider contraTransactionsProvider) {
        validate(bookingTemplate);

        List<InternalTransaction> bookingTransactions = mapToTransactions(bookingTemplate);
        List<InternalTransaction> contraTransactions = contraTransactionsProvider
            .calculateContraTransactions(bookingTransactions, bookingTemplate.getBookingDate(), bookingTemplate.getValueDate());

        List<InternalTransaction> transactions = new ArrayList<>();
        transactions.addAll(bookingTransactions);
        transactions.addAll(contraTransactions);

        for (int i = 0; i < transactions.size(); i++) {
            for (int j = i + 1; j < transactions.size(); j++) {
                transactions.get(i).link(transactions.get(j));
            }
        }

        transactions.forEach(this::setBalanceAfter);
        bookingTransactions.forEach(this::checkBalances);
        transactions.forEach(this::updateBalanceInLaterTransactions);

        transactionRepository.saveAll(transactions);
    }

    private void checkBalances(InternalTransaction transaction) {
        boolean illegalBalance = switch (transaction.getTransactionType()) {
            case DEBIT, PURCHASE, TRANSFER -> compare(transaction.getValue()).isNegative() && compare(transaction.getBalanceAfter()).isNegative();
            default -> false;
        };
        if (illegalBalance) {
            throw new InsufficientFundsException();
        }
    }

    private void setBalanceAfter(InternalTransaction transaction) {
        transactionBookService.setBalanceAfter(transaction);
    }

    private List<InternalTransaction> mapToTransactions(BookingTemplate bookingTemplate) {
        return bookingTemplate.getTransactionTemplates().stream()
            .map(transactionTemplate -> new InternalTransaction()
                .transactionType(transactionTemplate.getType())
                .bookingDate(bookingTemplate.getBookingDate())
                .valueDate(bookingTemplate.getValueDate())
                .value(transactionTemplate.getValue())
                .transactionBook(transactionTemplate.getTransactionBook())
                .description(transactionTemplate.getDescription())
                .issuer(transactionTemplate.getIssuer())
                .serviceQulifier(transactionTemplate.getServiceQualifier())
                .recipient(transactionTemplate.getRecipient()))
            .collect(Collectors.toList());
    }

    private void validate(InternalTransaction transaction) {
        if (transaction.getTransactionType() == TransactionType.CREDIT) {
            if (compare(transaction.getValue()).isNegative()) {
                throw new IllegalArgumentException(
                    String.format("InternalTransaction type %s requires the value not to be negative, but was %s", transaction.getTransactionType(),
                        transaction.getValue()));
            }
        } else if (transaction.getTransactionType() == TransactionType.DEBIT
            || transaction.getTransactionType() == TransactionType.FEE
            || transaction.getTransactionType() == TransactionType.PURCHASE) {
            if (compare(transaction.getValue()).isPositive()) {
                throw new IllegalArgumentException(
                    String.format("InternalTransaction type %s requires the value not to be positive, but was %s", transaction.getTransactionType(),
                        transaction.getValue()));
            }
        }
    }

    private void validate(BookingTemplate bookingTemplate) {
        // assert no multiple transactions per transaction book
        Set<TransactionBook> transactionTemplateSet = new HashSet<>();
        for (TransactionTemplate transactionTemplate : bookingTemplate.getTransactionTemplates()) {
            if (!transactionTemplateSet.add(transactionTemplate.getTransactionBook())) {
                throw new IllegalArgumentException(
                    "Booking template contains multiple transactions for transaction book " + transactionTemplate.getTransactionBook());
            }
        }

        // assert transaction kind and value match
        for (TransactionTemplate transactionTemplate : bookingTemplate.getTransactionTemplates()) {
            if (transactionTemplate.getType() == TransactionType.CREDIT) {
                if (compare(transactionTemplate.getValue()).isNegative()) {
                    throw new IllegalArgumentException(
                        String.format("InternalTransaction type %s requires the value not to be negative, but was %s", transactionTemplate.getType(),
                            transactionTemplate.getValue()));
                }
            } else if (transactionTemplate.getType() == TransactionType.DEBIT
                || transactionTemplate.getType() == TransactionType.FEE
                || transactionTemplate.getType() == TransactionType.PURCHASE) {
                if (compare(transactionTemplate.getValue()).isPositive()) {
                    throw new IllegalArgumentException(
                        String.format("InternalTransaction type %s requires the value not to be positive, but was %s", transactionTemplate.getType(),
                            transactionTemplate.getValue()));
                }
            }
        }
    }
}

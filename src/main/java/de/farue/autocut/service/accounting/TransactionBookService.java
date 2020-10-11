package de.farue.autocut.service.accounting;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionBookType;
import de.farue.autocut.domain.enumeration.TransactionKind;
import de.farue.autocut.repository.TransactionBookRepository;
import de.farue.autocut.repository.TransactionRepository;
import de.farue.autocut.service.TransactionService;
import de.farue.autocut.utils.BigDecimalUtil;

/**
 * Service Implementation for managing {@link TransactionBook}.
 */
@Service
@Transactional
public class TransactionBookService {

    private final Logger log = LoggerFactory.getLogger(TransactionBookService.class);

    private static final String OWN_ACCOUNT_NAME = "FaRue Account";

    private final TransactionBookRepository transactionBookRepository;

    private final TransactionRepository transactionRepository;

    private final TransactionService transactionService;

    public TransactionBookService(TransactionBookRepository transactionBookRepository, TransactionRepository transactionRepository,
        TransactionService transactionService) {
        this.transactionBookRepository = transactionBookRepository;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    /**
     * Save a transactionBook.
     *
     * @param transactionBook the entity to save.
     * @return the persisted entity.
     */
    public TransactionBook save(TransactionBook transactionBook) {
        log.debug("Request to save TransactionBook : {}", transactionBook);
        return transactionBookRepository.save(transactionBook);
    }

    /**
     * Get all the transactionBooks.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TransactionBook> findAll() {
        log.debug("Request to get all TransactionBooks");
        return transactionBookRepository.findAll();
    }


    /**
     * Get one transactionBook by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TransactionBook> findOne(Long id) {
        log.debug("Request to get TransactionBook : {}", id);
        return transactionBookRepository.findById(id);
    }

    /**
     * Delete the transactionBook by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TransactionBook : {}", id);
        transactionBookRepository.deleteById(id);
    }

    protected TransactionBook getOwnCashTransactionBook() {
        return transactionBookRepository.findOneByNameAndType(OWN_ACCOUNT_NAME, TransactionBookType.CASH)
            .orElseGet(() -> transactionBookRepository.save(new TransactionBook().name(OWN_ACCOUNT_NAME).type(TransactionBookType.CASH)));
    }

    protected TransactionBook getOwnRevenueTransactionBook() {
        return transactionBookRepository.findOneByNameAndType(OWN_ACCOUNT_NAME, TransactionBookType.REVENUE)
            .orElseGet(() -> transactionBookRepository.save(new TransactionBook().name(OWN_ACCOUNT_NAME).type(TransactionBookType.REVENUE)));
    }

    @Transactional(readOnly = true)
    public BigDecimal getCurrentBalance(TransactionBook transactionBook) {
        return getBalanceOn(transactionBook, Instant.now());
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalanceOn(TransactionBook transactionBook, Instant time) {
        return transactionRepository.findFirstByTransactionBookBefore(transactionBook, time, PageRequest.of(0, 1))
            .stream()
            .map(Transaction::getBalanceAfter)
            .findFirst()
            .orElse(BigDecimal.ZERO);
    }

    private BigDecimal getBalanceOnWithLock(TransactionBook transactionBook, Instant time) {
        return transactionRepository.findFirstByTransactionBookBeforeWithLock(transactionBook, time, PageRequest.of(0, 1))
            .stream()
            .map(Transaction::getBalanceAfter)
            .findFirst()
            .orElse(BigDecimal.ZERO);
    }

    public void saveBankAccountActivity(BookingTemplate bookingTemplate) {
        // TODO #7:
        //  - require booking template to only create bookings for own cash account
        //  - create new ContraTransactionsProvider that links transaction to correct transaction book
        //    - possibly chain-of-responsibility: first try to match name + apt. no., then match transaction book no.
        saveBooking(bookingTemplate);
    }

    public void saveMemberBooking(BookingTemplate bookingTemplate) {
        saveBooking(bookingTemplate, new MemberBookingContraTransactionProvider(getOwnCashTransactionBook(), getOwnRevenueTransactionBook()));
    }

    private void saveBooking(BookingTemplate bookingTemplate) {
        saveBooking(bookingTemplate, new EmptyContraTransactionsProvider());
    }

    private void saveBooking(BookingTemplate bookingTemplate, ContraTransactionsProvider contraTransactionsProvider) {
        validate(bookingTemplate);

        List<Transaction> bookingTransactions = mapToTransactions(bookingTemplate);
        List<Transaction> contraTransactions = contraTransactionsProvider
            .calculateContraTransactions(bookingTransactions, bookingTemplate.getBookingDate(), bookingTemplate.getValueDate());

        List<Transaction> transactions = new ArrayList<>();
        transactions.addAll(bookingTransactions);
        transactions.addAll(contraTransactions);

        for (int i = 0; i < transactions.size(); i++) {
            for (int j = i + 1; j < transactions.size(); j++) {
                transactions.get(i).link(transactions.get(j));
            }
        }

        transactions.forEach(this::setBalanceAfter);
        transactions.forEach(transactionService::updateBalanceInLaterTransactions);

        transactionRepository.saveAll(transactions);
    }

    private void setBalanceAfter(Transaction transaction) {
        BigDecimal lastBalance = getBalanceOnWithLock(transaction.getTransactionBook(), transaction.getValueDate());
        BigDecimal newBalance = lastBalance.add(transaction.getValue());
        transaction.setBalanceAfter(newBalance);
    }

    private List<Transaction> mapToTransactions(BookingTemplate bookingTemplate) {
        return bookingTemplate.getTransactionTemplates().stream()
            .map(transactionTemplate -> new Transaction()
                .kind(transactionTemplate.getKind())
                .bookingDate(bookingTemplate.getBookingDate())
                .valueDate(bookingTemplate.getValueDate())
                .value(transactionTemplate.getValue())
                .transactionBook(transactionTemplate.getTransactionBook())
                .description(transactionTemplate.getDescription())
                .issuer(transactionTemplate.getIssuer())
                .recipient(transactionTemplate.getRecipient()))
            .collect(Collectors.toList());
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
            if (transactionTemplate.getKind() == TransactionKind.CREDIT) {
                if (BigDecimalUtil.isNegative(transactionTemplate.getValue())) {
                    throw new IllegalArgumentException(
                        String.format("Transaction kind %s requires the value not to be negative, but was %s", transactionTemplate.getKind(),
                            transactionTemplate.getValue()));
                }
            } else if (transactionTemplate.getKind() == TransactionKind.DEBIT
                || transactionTemplate.getKind() == TransactionKind.FEE
                || transactionTemplate.getKind() == TransactionKind.PURCHASE) {
                if (BigDecimalUtil.isPositive(transactionTemplate.getValue())) {
                    throw new IllegalArgumentException(
                        String.format("Transaction kind %s requires the value not to be positive, but was %s", transactionTemplate.getKind(),
                            transactionTemplate.getValue()));
                }
            }
        }
    }
}

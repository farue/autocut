package de.farue.autocut.service.accounting;

import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionBookType;
import de.farue.autocut.repository.TransactionBookRepository;
import de.farue.autocut.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TransactionBook}.
 */
@Service
@Transactional
public class TransactionBookService {

    private final Logger log = LoggerFactory.getLogger(TransactionBookService.class);

    private static final String OWN_ACCOUNT_NAME = "FaRue Account";

    private final TransactionBookRepository transactionBookRepository;
    private final TransactionRepository<Transaction> transactionRepository;

    public TransactionBookService(
        TransactionBookRepository transactionBookRepository,
        TransactionRepository<Transaction> transactionRepository
    ) {
        this.transactionBookRepository = transactionBookRepository;
        this.transactionRepository = transactionRepository;
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
     * Partially update a transactionBook.
     *
     * @param transactionBook the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TransactionBook> partialUpdate(TransactionBook transactionBook) {
        log.debug("Request to partially update TransactionBook : {}", transactionBook);

        return transactionBookRepository
            .findById(transactionBook.getId())
            .map(existingTransactionBook -> {
                if (transactionBook.getName() != null) {
                    existingTransactionBook.setName(transactionBook.getName());
                }
                if (transactionBook.getType() != null) {
                    existingTransactionBook.setType(transactionBook.getType());
                }

                return existingTransactionBook;
            })
            .map(transactionBookRepository::save);
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

    public TransactionBook getOwnCashTransactionBook() {
        return transactionBookRepository
            .findOneByNameAndType(OWN_ACCOUNT_NAME, TransactionBookType.CASH)
            .orElseGet(() -> transactionBookRepository.save(new TransactionBook().name(OWN_ACCOUNT_NAME).type(TransactionBookType.CASH)));
    }

    public TransactionBook getOwnRevenueTransactionBook() {
        return transactionBookRepository
            .findOneByNameAndType(OWN_ACCOUNT_NAME, TransactionBookType.REVENUE)
            .orElseGet(
                () -> transactionBookRepository.save(new TransactionBook().name(OWN_ACCOUNT_NAME).type(TransactionBookType.REVENUE))
            );
    }

    @Transactional(readOnly = true)
    public BigDecimal getCurrentBalance(TransactionBook transactionBook) {
        return getBalanceOn(transactionBook, Instant.now());
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalanceOn(TransactionBook transactionBook, Instant time) {
        return transactionRepository
            .findFirstByTransactionBookBefore(transactionBook, time, PageRequest.of(0, 1))
            .stream()
            .map(Transaction::getBalanceAfter)
            .findFirst()
            .orElse(BigDecimal.ZERO);
    }

    private BigDecimal getBalanceOnWithLock(TransactionBook transactionBook, Instant time) {
        return transactionRepository
            .findFirstByTransactionBookBeforeWithLock(transactionBook, time, PageRequest.of(0, 1))
            .stream()
            .map(Transaction::getBalanceAfter)
            .findFirst()
            .orElse(BigDecimal.ZERO);
    }

    public void setBalanceAfter(Transaction transaction) {
        BigDecimal lastBalance = getBalanceOnWithLock(transaction.getTransactionBook(), transaction.getValueDate());
        BigDecimal newBalance = lastBalance.add(transaction.getValue());
        transaction.setBalanceAfter(newBalance);
    }
}

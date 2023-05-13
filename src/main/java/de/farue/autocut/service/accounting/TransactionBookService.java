package de.farue.autocut.service.accounting;

import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionBookType;
import de.farue.autocut.repository.TransactionBookRepository;
import de.farue.autocut.repository.TransactionRepository;
import de.farue.autocut.security.AuthoritiesConstants;
import de.farue.autocut.security.SecurityUtils;
import de.farue.autocut.service.LeaseService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
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

    private final TransactionBookRepository transactionBookRepository;
    private final TransactionRepository<Transaction> transactionRepository;
    private LeaseService leaseService;

    public TransactionBookService(
        TransactionBookRepository transactionBookRepository,
        TransactionRepository<Transaction> transactionRepository,
        @Lazy LeaseService leaseService
    ) {
        this.transactionBookRepository = transactionBookRepository;
        this.transactionRepository = transactionRepository;
        this.leaseService = leaseService;
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
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.VIEW_TRANSACTIONS)) {
            return transactionBookRepository.findAll();
        }
        return getCurrentUserTransactionBooks().orElse(new ArrayList<>());
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
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.VIEW_TRANSACTIONS)) {
            return transactionBookRepository.findById(id);
        }
        return getCurrentUserTransactionBooks().flatMap(list -> list.stream().filter(tb -> tb.getId().equals(id)).findFirst());
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

    public Optional<TransactionBook> getCurrentUserCashTransactionBook() {
        return getCurrentUserTransactionBooks()
            .flatMap(transactionBooks ->
                transactionBooks.stream().filter(transactionBook -> transactionBook.getType() == TransactionBookType.CASH).findFirst()
            );
    }

    public Optional<List<TransactionBook>> getCurrentUserTransactionBooks() {
        return leaseService.getCurrentUserLease().map(lease -> new ArrayList<>(lease.getTransactionBooks()));
    }
}

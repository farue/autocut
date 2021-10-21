package de.farue.autocut.service;

import static de.farue.autocut.utils.BigDecimalUtil.compare;

import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.Transaction_;
import de.farue.autocut.repository.TransactionRepository;
import de.farue.autocut.utils.DateUtil;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Transaction}.
 */
@Service
@Transactional
public abstract class TransactionService<T extends Transaction> {

    private final Logger log = LoggerFactory.getLogger(TransactionService.class);

    protected abstract TransactionRepository<T> getRepository();

    protected abstract void partialUpdate(T existingTransaction, T transaction);

    /**
     * Save a transaction.
     *
     * @param transaction the entity to save.
     * @return the persisted entity.
     */
    public T save(T transaction) {
        log.debug("Request to save Transaction : {}", transaction);

        // set/update balanceAfter
        BigDecimal previousBalanceAfter = findTransactionImmediatelyBefore(transaction)
            .map(Transaction::getBalanceAfter)
            .orElse(BigDecimal.ZERO);
        BigDecimal calculatedBalanceAfter = previousBalanceAfter.add(transaction.getValue());
        if (transaction.getBalanceAfter() != null && compare(transaction.getBalanceAfter()).isNotEqualTo(calculatedBalanceAfter)) {
            log.debug(
                "Overwriting balanceAfter in transaction. Old value was {}, new calculated value is {}",
                transaction.getBalanceAfter(),
                calculatedBalanceAfter
            );
        }
        transaction.setBalanceAfter(calculatedBalanceAfter);

        boolean update = transaction.getId() != null;
        T savedTransaction = getRepository().save(transaction);

        //        BigDecimal initialBalance = update ? findTransactionImmediatelyBefore(transaction).map(Transaction::getBalanceAfter).orElse(BigDecimal.ZERO)
        //            : savedTransaction.getBalanceAfter();
        BigDecimal initialBalance = savedTransaction.getBalanceAfter();
        updateBalanceInLaterTransactions(
            savedTransaction.getTransactionBook(),
            savedTransaction.getValueDate(),
            savedTransaction.getId(),
            initialBalance
        );
        return savedTransaction;
    }

    /**
     * Partially update a transaction.
     *
     * @param transaction the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<T> partialUpdate(T transaction) {
        log.debug("Request to partially update Transaction : {}", transaction);

        return getRepository()
            .findById(transaction.getId())
            .map(existingTransaction -> {
                if (transaction.getType() != null) {
                    existingTransaction.setType(transaction.getType());
                }
                if (transaction.getBookingDate() != null) {
                    existingTransaction.setBookingDate(transaction.getBookingDate());
                }
                if (transaction.getValueDate() != null) {
                    existingTransaction.setValueDate(transaction.getValueDate());
                }
                if (transaction.getValue() != null) {
                    existingTransaction.setValue(transaction.getValue());
                }
                if (transaction.getBalanceAfter() != null) {
                    existingTransaction.setBalanceAfter(transaction.getBalanceAfter());
                }
                if (transaction.getDescription() != null) {
                    existingTransaction.setDescription(transaction.getDescription());
                }
                if (transaction.getServiceQulifier() != null) {
                    existingTransaction.setServiceQulifier(transaction.getServiceQulifier());
                }
                partialUpdate(existingTransaction, transaction);

                return existingTransaction;
            })
            .map(getRepository()::save);
    }

    /**
     * Get all the transactions.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<T> findAll() {
        log.debug("Request to get all Transactions");
        return getRepository().findAllWithEagerRelationships();
    }

    /**
     * Get all the internalTransactions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {
        log.debug("Request to get all InternalTransactions");
        return getRepository().findAll(pageable);
    }

    /**
     * Get all the transactions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<T> findAllWithEagerRelationships(Pageable pageable) {
        return getRepository().findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one transaction by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<T> findOne(Long id) {
        log.debug("Request to get Transaction : {}", id);
        return getRepository().findOneWithEagerRelationships(id);
    }

    /**
     * Delete the transaction by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        T transaction = findOne(id).orElseThrow(() -> new IllegalArgumentException("Transaction does not exist: " + id));
        delete(transaction);
    }

    public void delete(T transaction) {
        log.debug("Request to delete Transaction : {}", transaction);

        Optional<T> firstTransactionBefore = findTransactionImmediatelyBefore(transaction);
        getRepository().flush();
        getRepository().delete(transaction);
        getRepository().flush();
        updateBalanceInLaterTransactions(
            transaction.getTransactionBook(),
            transaction.getValueDate(),
            transaction.getId(),
            firstTransactionBefore.isPresent() ? firstTransactionBefore.get().getBalanceAfter() : BigDecimal.ZERO
        );
    }

    @Transactional(readOnly = true)
    public Page<T> findAllForTransactionBook(TransactionBook transactionBook, Pageable pageable) {
        return getRepository().findAllByTransactionBook(transactionBook, pageable);
    }

    @Transactional(readOnly = true)
    public Page<T> findAllForTransactionBook(TransactionBook transactionBook, Instant from, Instant until, Pageable pageable) {
        from = from != null ? from : DateUtil.MIN_INSTANT;
        until = until != null ? until : DateUtil.MAX_INSTANT;
        return getRepository().findAllByTransactionBookBetween(transactionBook, from, until, pageable);
    }

    public List<T> findAllForTransactionBookWithLinks(TransactionBook transactionBook) {
        return getRepository().findAllByTransactionBookWithEagerRelationships(transactionBook);
    }

    protected Optional<T> findTransactionImmediatelyBefore(T transaction) {
        return getRepository()
            .findAllOlderThanWithLock(
                transaction.getTransactionBook(),
                transaction.getValueDate(),
                transaction.getId() != null ? transaction.getId() : Long.MAX_VALUE,
                PageRequest.of(0, 1, Sort.by(Order.desc(Transaction_.VALUE_DATE), Order.desc(Transaction_.ID)))
            )
            .stream()
            .findFirst();
    }

    protected List<T> findAllAfter(T transaction) {
        return findAllAfter(
            transaction.getTransactionBook(),
            transaction.getValueDate(),
            transaction.getId() != null ? transaction.getId() : Long.MAX_VALUE
        );
    }

    protected List<T> findAllAfter(TransactionBook transactionBook, Instant valueDate, long id) {
        return getRepository()
            .findAllNewerThanWithLock(
                transactionBook,
                valueDate,
                id,
                PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Order.asc(Transaction_.VALUE_DATE), Order.asc(Transaction_.ID)))
            );
    }

    protected void updateBalanceInLaterTransactions(T transaction) {
        updateBalanceInLaterTransactions(
            transaction.getTransactionBook(),
            transaction.getValueDate(),
            transaction.getId() != null ? transaction.getId() : Long.MAX_VALUE,
            transaction.getBalanceAfter()
        );
    }

    protected void updateBalanceInLaterTransactions(
        TransactionBook transactionBook,
        Instant valueDate,
        long id,
        BigDecimal initialBalance
    ) {
        List<T> laterTransactions = findAllAfter(transactionBook, valueDate, id);
        BigDecimal balance = initialBalance;
        for (T t : laterTransactions) {
            balance = balance.add(t.getValue());
            t.setBalanceAfter(balance);
        }
        getRepository().saveAll(laterTransactions);
    }
}

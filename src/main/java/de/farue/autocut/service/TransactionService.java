package de.farue.autocut.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.repository.TransactionRepository;

/**
 * Service Implementation for managing {@link Transaction}.
 */
@Service
@Transactional
public class TransactionService {

    private final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Save a transaction.
     *
     * @param transaction the entity to save.
     * @return the persisted entity.
     */
    public Transaction save(Transaction transaction) {
        log.debug("Request to save Transaction : {}", transaction);
        Transaction savedTransaction = transactionRepository.save(transaction);
        updateBalanceInLaterTransactions(savedTransaction);
        return savedTransaction;
    }

    /**
     * Get all the transactions.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Transaction> findAll() {
        log.debug("Request to get all Transactions");
        return transactionRepository.findAllWithEagerRelationships();
    }


    /**
     * Get all the transactions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Transaction> findAllWithEagerRelationships(Pageable pageable) {
        return transactionRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one transaction by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Transaction> findOne(Long id) {
        log.debug("Request to get Transaction : {}", id);
        return transactionRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the transaction by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Transaction : {}", id);

        transactionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findAllForTransactionBook(TransactionBook transactionBook, Pageable pageable) {
        return transactionRepository.findAllByTransactionBookOrderByValueDateDesc(transactionBook, pageable);
    }

    public void updateBalanceInLaterTransactions(Transaction transaction) {
        List<Transaction> laterTransactions = transactionRepository
            .findAllNewerThanWithLock(transaction.getTransactionBook(), transaction.getValueDate(), transaction.getId() != null ? transaction.getId() : 0);
        BigDecimal balance = transaction.getBalanceAfter();
        for (Transaction t : laterTransactions) {
            balance = balance.add(t.getValue());
            t.setBalanceAfter(balance);
        }
        transactionRepository.saveAll(laterTransactions);
    }
}

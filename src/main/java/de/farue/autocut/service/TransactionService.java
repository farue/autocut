package de.farue.autocut.service;

import de.farue.autocut.domain.Transaction;
import de.farue.autocut.repository.TransactionRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return transactionRepository.save(transaction);
    }

    /**
     * Partially update a transaction.
     *
     * @param transaction the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Transaction> partialUpdate(Transaction transaction) {
        log.debug("Request to partially update Transaction : {}", transaction);

        return transactionRepository
            .findById(transaction.getId())
            .map(existingTransaction -> {
                if (transaction.getKind() != null) {
                    existingTransaction.setKind(transaction.getKind());
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
                if (transaction.getIssuer() != null) {
                    existingTransaction.setIssuer(transaction.getIssuer());
                }
                if (transaction.getRecipient() != null) {
                    existingTransaction.setRecipient(transaction.getRecipient());
                }

                return existingTransaction;
            })
            .map(transactionRepository::save);
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
}

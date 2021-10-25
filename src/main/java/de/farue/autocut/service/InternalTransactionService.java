package de.farue.autocut.service;

import de.farue.autocut.domain.InternalTransaction;
import de.farue.autocut.repository.InternalTransactionRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link InternalTransaction}.
 */
@Service
@Transactional
public class InternalTransactionService {

    private final Logger log = LoggerFactory.getLogger(InternalTransactionService.class);

    private final InternalTransactionRepository internalTransactionRepository;

    public InternalTransactionService(InternalTransactionRepository internalTransactionRepository) {
        this.internalTransactionRepository = internalTransactionRepository;
    }

    /**
     * Save a internalTransaction.
     *
     * @param internalTransaction the entity to save.
     * @return the persisted entity.
     */
    public InternalTransaction save(InternalTransaction internalTransaction) {
        log.debug("Request to save InternalTransaction : {}", internalTransaction);
        return internalTransactionRepository.save(internalTransaction);
    }

    /**
     * Partially update a internalTransaction.
     *
     * @param internalTransaction the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<InternalTransaction> partialUpdate(InternalTransaction internalTransaction) {
        log.debug("Request to partially update InternalTransaction : {}", internalTransaction);

        return internalTransactionRepository
            .findById(internalTransaction.getId())
            .map(existingInternalTransaction -> {
                if (internalTransaction.getType() != null) {
                    existingInternalTransaction.setType(internalTransaction.getType());
                }
                if (internalTransaction.getBookingDate() != null) {
                    existingInternalTransaction.setBookingDate(internalTransaction.getBookingDate());
                }
                if (internalTransaction.getValueDate() != null) {
                    existingInternalTransaction.setValueDate(internalTransaction.getValueDate());
                }
                if (internalTransaction.getValue() != null) {
                    existingInternalTransaction.setValue(internalTransaction.getValue());
                }
                if (internalTransaction.getBalanceAfter() != null) {
                    existingInternalTransaction.setBalanceAfter(internalTransaction.getBalanceAfter());
                }
                if (internalTransaction.getDescription() != null) {
                    existingInternalTransaction.setDescription(internalTransaction.getDescription());
                }
                if (internalTransaction.getServiceQulifier() != null) {
                    existingInternalTransaction.setServiceQulifier(internalTransaction.getServiceQulifier());
                }
                if (internalTransaction.getIssuer() != null) {
                    existingInternalTransaction.setIssuer(internalTransaction.getIssuer());
                }
                if (internalTransaction.getRecipient() != null) {
                    existingInternalTransaction.setRecipient(internalTransaction.getRecipient());
                }

                return existingInternalTransaction;
            })
            .map(internalTransactionRepository::save);
    }

    /**
     * Get all the internalTransactions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<InternalTransaction> findAll(Pageable pageable) {
        log.debug("Request to get all InternalTransactions");
        return internalTransactionRepository.findAll(pageable);
    }

    /**
     * Get all the internalTransactions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<InternalTransaction> findAllWithEagerRelationships(Pageable pageable) {
        return internalTransactionRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one internalTransaction by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<InternalTransaction> findOne(Long id) {
        log.debug("Request to get InternalTransaction : {}", id);
        return internalTransactionRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the internalTransaction by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete InternalTransaction : {}", id);
        internalTransactionRepository.deleteById(id);
    }
}

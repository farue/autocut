package de.farue.autocut.service;

import de.farue.autocut.domain.Association;
import de.farue.autocut.domain.BankAccount;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionBookType;
import de.farue.autocut.repository.AssociationRepository;
import de.farue.autocut.repository.TransactionBookRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Association}.
 */
@Service
@Transactional
public class AssociationService {

    private final Logger log = LoggerFactory.getLogger(AssociationService.class);

    private final AssociationRepository associationRepository;
    private final TransactionBookRepository transactionBookRepository;

    public AssociationService(AssociationRepository associationRepository, TransactionBookRepository transactionBookRepository) {
        this.associationRepository = associationRepository;
        this.transactionBookRepository = transactionBookRepository;
    }

    /**
     * Save a association.
     *
     * @param association the entity to save.
     * @return the persisted entity.
     */
    public Association save(Association association) {
        log.debug("Request to save Association : {}", association);
        return associationRepository.save(association);
    }

    /**
     * Partially update a association.
     *
     * @param association the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Association> partialUpdate(Association association) {
        log.debug("Request to partially update Association : {}", association);

        return associationRepository
            .findById(association.getId())
            .map(existingAssociation -> {
                if (association.getName() != null) {
                    existingAssociation.setName(association.getName());
                }
                if (association.getActive() != null) {
                    existingAssociation.setActive(association.getActive());
                }

                return existingAssociation;
            })
            .map(associationRepository::save);
    }

    /**
     * Get all the associations.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Association> findAll() {
        log.debug("Request to get all Associations");
        return associationRepository.findAll();
    }

    /**
     * Get one association by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Association> findOne(Long id) {
        log.debug("Request to get Association : {}", id);
        return associationRepository.findById(id);
    }

    /**
     * Delete the association by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Association : {}", id);
        associationRepository.deleteById(id);
    }

    public Association getActiveAssociation() {
        log.debug("Request to get active Association");
        return associationRepository.findByActiveTrue().orElseThrow(() -> new RuntimeException("No active association found"));
    }

    public TransactionBook getCashTransactionBook() {
        log.debug("Request to get cash transaction book");
        return Optional
            .ofNullable(getActiveAssociation().getCashTransactionBook())
            .orElseGet(() -> transactionBookRepository.save(new TransactionBook().type(TransactionBookType.CASH)));
    }

    public TransactionBook getRevenueTransactionBook() {
        log.debug("Request to get revenue transaction book");
        return Optional
            .ofNullable(getActiveAssociation().getRevenueTransactionBook())
            .orElseGet(() -> transactionBookRepository.save(new TransactionBook().type(TransactionBookType.REVENUE)));
    }

    public BankAccount getBankAccount() {
        log.debug("Request to get bank account");
        return Optional
            .ofNullable(getActiveAssociation().getBankAccount())
            .orElseThrow(() -> new RuntimeException("No bank account found for active association"));
    }
}

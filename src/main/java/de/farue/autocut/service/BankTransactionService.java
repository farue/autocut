package de.farue.autocut.service;

import de.farue.autocut.domain.BankTransaction;
import de.farue.autocut.repository.BankTransactionRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BankTransaction}.
 */
@Service
@Transactional
public class BankTransactionService {

    private final Logger log = LoggerFactory.getLogger(BankTransactionService.class);

    private final BankTransactionRepository bankTransactionRepository;

    public BankTransactionService(BankTransactionRepository bankTransactionRepository) {
        this.bankTransactionRepository = bankTransactionRepository;
    }

    /**
     * Save a bankTransaction.
     *
     * @param bankTransaction the entity to save.
     * @return the persisted entity.
     */
    public BankTransaction save(BankTransaction bankTransaction) {
        log.debug("Request to save BankTransaction : {}", bankTransaction);
        return bankTransactionRepository.save(bankTransaction);
    }

    /**
     * Partially update a bankTransaction.
     *
     * @param bankTransaction the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BankTransaction> partialUpdate(BankTransaction bankTransaction) {
        log.debug("Request to partially update BankTransaction : {}", bankTransaction);

        return bankTransactionRepository
            .findById(bankTransaction.getId())
            .map(existingBankTransaction -> {
                if (bankTransaction.getBookingDate() != null) {
                    existingBankTransaction.setBookingDate(bankTransaction.getBookingDate());
                }
                if (bankTransaction.getValueDate() != null) {
                    existingBankTransaction.setValueDate(bankTransaction.getValueDate());
                }
                if (bankTransaction.getValue() != null) {
                    existingBankTransaction.setValue(bankTransaction.getValue());
                }
                if (bankTransaction.getBalanceAfter() != null) {
                    existingBankTransaction.setBalanceAfter(bankTransaction.getBalanceAfter());
                }
                if (bankTransaction.getType() != null) {
                    existingBankTransaction.setType(bankTransaction.getType());
                }
                if (bankTransaction.getDescription() != null) {
                    existingBankTransaction.setDescription(bankTransaction.getDescription());
                }
                if (bankTransaction.getCustomerRef() != null) {
                    existingBankTransaction.setCustomerRef(bankTransaction.getCustomerRef());
                }
                if (bankTransaction.getGvCode() != null) {
                    existingBankTransaction.setGvCode(bankTransaction.getGvCode());
                }
                if (bankTransaction.getEndToEnd() != null) {
                    existingBankTransaction.setEndToEnd(bankTransaction.getEndToEnd());
                }
                if (bankTransaction.getPrimanota() != null) {
                    existingBankTransaction.setPrimanota(bankTransaction.getPrimanota());
                }
                if (bankTransaction.getCreditor() != null) {
                    existingBankTransaction.setCreditor(bankTransaction.getCreditor());
                }
                if (bankTransaction.getMandate() != null) {
                    existingBankTransaction.setMandate(bankTransaction.getMandate());
                }

                return existingBankTransaction;
            })
            .map(bankTransactionRepository::save);
    }

    /**
     * Get all the bankTransactions.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BankTransaction> findAll() {
        log.debug("Request to get all BankTransactions");
        return bankTransactionRepository.findAllWithEagerRelationships();
    }

    /**
     * Get all the bankTransactions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<BankTransaction> findAllWithEagerRelationships(Pageable pageable) {
        return bankTransactionRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one bankTransaction by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BankTransaction> findOne(Long id) {
        log.debug("Request to get BankTransaction : {}", id);
        return bankTransactionRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the bankTransaction by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BankTransaction : {}", id);
        bankTransactionRepository.deleteById(id);
    }
}

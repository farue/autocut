package de.farue.autocut.service;

import de.farue.autocut.domain.BankAccount;
import de.farue.autocut.repository.BankAccountRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BankAccount}.
 */
@Service
@Transactional
public class BankAccountService {

    private final Logger log = LoggerFactory.getLogger(BankAccountService.class);

    private final BankAccountRepository bankAccountRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    /**
     * Save a bankAccount.
     *
     * @param bankAccount the entity to save.
     * @return the persisted entity.
     */
    public BankAccount save(BankAccount bankAccount) {
        log.debug("Request to save BankAccount : {}", bankAccount);
        return bankAccountRepository.save(bankAccount);
    }

    /**
     * Partially update a bankAccount.
     *
     * @param bankAccount the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BankAccount> partialUpdate(BankAccount bankAccount) {
        log.debug("Request to partially update BankAccount : {}", bankAccount);

        return bankAccountRepository
            .findById(bankAccount.getId())
            .map(existingBankAccount -> {
                if (bankAccount.getName() != null) {
                    existingBankAccount.setName(bankAccount.getName());
                }
                if (bankAccount.getBic() != null) {
                    existingBankAccount.setBic(bankAccount.getBic());
                }
                if (bankAccount.getIban() != null) {
                    existingBankAccount.setIban(bankAccount.getIban());
                }

                return existingBankAccount;
            })
            .map(bankAccountRepository::save);
    }

    /**
     * Get all the bankAccounts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BankAccount> findAll() {
        log.debug("Request to get all BankAccounts");
        return bankAccountRepository.findAll();
    }

    /**
     * Get one bankAccount by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BankAccount> findOne(Long id) {
        log.debug("Request to get BankAccount : {}", id);
        return bankAccountRepository.findById(id);
    }

    /**
     * Delete the bankAccount by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BankAccount : {}", id);
        bankAccountRepository.deleteById(id);
    }
}

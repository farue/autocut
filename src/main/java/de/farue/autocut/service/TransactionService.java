package de.farue.autocut.service;

import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.User;
import de.farue.autocut.repository.LeaseRepository;
import de.farue.autocut.repository.TenantRepository;
import de.farue.autocut.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Transaction}.
 */
@Service
@Transactional
public class TransactionService {

    private final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final LeaseRepository leaseRepository;
    private final TenantRepository tenantRepository;

    public TransactionService(TransactionRepository transactionRepository, LeaseRepository leaseRepository,
                              TenantRepository tenantRepository) {
        this.transactionRepository = transactionRepository;
        this.leaseRepository = leaseRepository;
        this.tenantRepository = tenantRepository;
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
     * Get all the transactions.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Transaction> findAll() {
        log.debug("Request to get all Transactions");
        return transactionRepository.findAll();
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
        return transactionRepository.findById(id);
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

    public BigDecimal getCurrentBalance(User user) {
        return tenantRepository.findOneByUser(user)
            .map(this::getCurrentBalance)
            .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getCurrentBalance(Tenant tenant) {
        return leaseRepository.findOneByTenants(tenant)
            .map(this::getCurrentBalance)
            .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getCurrentBalance(Lease lease) {
        return transactionRepository.findFirstByLeaseOrderByIdDesc(lease)
            .map(Transaction::getBalanceAfter)
            .orElse(BigDecimal.ZERO);
    }

    public void setBalanceAfter(Transaction transaction) {
        Lease lease = transaction.getLease();
        BigDecimal currentBalance = getCurrentBalance(lease);
        BigDecimal newBalance = currentBalance.add(transaction.getAmount());
        transaction.setBalanceAfter(newBalance);
    }

    public void addTransactionWithBalanceCheck(Transaction transaction) {
        Lease lease = transaction.getLease();
        BigDecimal currentBalance = getCurrentBalance(lease);
        if (currentBalance.add(transaction.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            // TODO: New exception class
            throw new RuntimeException("Not enough funds");
        }
        setBalanceAfter(transaction);
        addTransactionWithoutBalanceCheck(transaction);
    }

    public void addTransactionWithoutBalanceCheck(Transaction transaction) {
        transactionRepository.saveAndFlush(transaction);
    }
}

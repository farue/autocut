package de.farue.autocut.service;

import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.User;
import de.farue.autocut.repository.LeaseRepository;
import de.farue.autocut.repository.TenantRepository;
import de.farue.autocut.repository.TransactionRepository;
import de.farue.autocut.utils.BigDecimalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Transaction}.
 */
@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
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
        validateAndUpdateBalance(transaction);
        return transactionRepository.save(transaction);
    }

    /**
     * Save a transaction with balance check.
     *
     * @param transaction the entity to save.
     * @return the persisted entity.
     */
    public Transaction saveWithBalanceCheck(Transaction transaction) {
        log.debug("Request to save Transaction : {}", transaction);
        validateAndUpdateBalance(transaction);
        if (BigDecimalUtil.isNegative(transaction.getBalanceAfter())) {
            throw new InsufficientFundsException();
        }
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
        return updateAndGetCurrentBalance(lease);
    }

    public void setBalanceAfter(Transaction transaction) {
        Lease lease = transaction.getLease();
        BigDecimal currentBalance = getCurrentBalance(lease);
        BigDecimal newBalance = currentBalance.add(transaction.getValue());
        transaction.setBalanceAfter(newBalance);
    }

    /**
     * Updates balances for every lease once a day and verifies that they do not have a negative balance.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateBalances() {
        leaseRepository.findAll().forEach(lease -> {
            final BigDecimal currentBalance = getCurrentBalance(lease);
            if (BigDecimalUtil.isNegative(currentBalance)) {
                lease.setBlocked(true);
                // TODO: Send email
                leaseRepository.save(lease);
            }
        });
    }

    private BigDecimal updateAndGetCurrentBalance(Lease lease) {
        updateBalanceInFutureTransactions(lease);
        return getBalanceOfLastValuedTransaction(lease);
    }

    private void updateBalanceInFutureTransactions(Lease lease) {
        List<Transaction> orderedTransactions = transactionRepository
            .findAllByLeaseAndBalanceAfterIsNullAndValueDateLessThanEqualOrderByValueDateAscIdAsc(lease, Instant.now());
        BigDecimal balance = getBalanceOfLastValuedTransaction(lease);
        for (Transaction transaction : orderedTransactions) {
            balance = balance.add(transaction.getValue());
            transaction.setBalanceAfter(balance);
        }
        transactionRepository.saveAll(orderedTransactions);
    }

    private BigDecimal getBalanceOfLastValuedTransaction(Lease lease) {
        return transactionRepository
            .findFirstByLeaseAndBalanceAfterIsNotNullOrderByValueDateDescIdDesc(lease)
            .map(Transaction::getBalanceAfter)
            .orElse(BigDecimal.ZERO);
    }

    private void validateAndUpdateBalance(Transaction transaction) {
        if (transaction.getLease() != null) {
            if (transaction.getBalanceAfter() != null) {
                throw new IllegalArgumentException(
                    "Balance for lease transactions must be assigned by TransactionService");
            }
            if (!transaction.getValueDate().isAfter(Instant.now())) {
                final BigDecimal currentBalance = getCurrentBalance(transaction.getLease());
                transaction.setBalanceAfter(currentBalance.add(transaction.getValue()));
            }
        } else {
            if (transaction.getBalanceAfter() == null) {
                throw new IllegalArgumentException("Balance for non-lease transactions must be calculated in advance");
            }
        }
    }
}

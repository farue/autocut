package de.farue.autocut.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.User;
import de.farue.autocut.domain.enumeration.TransactionKind;
import de.farue.autocut.repository.LeaseRepository;
import de.farue.autocut.repository.TenantRepository;
import de.farue.autocut.repository.TransactionRepository;
import de.farue.autocut.repository.UserRepository;
import de.farue.autocut.security.SecurityUtils;
import de.farue.autocut.utils.BigDecimalUtil;

/**
 * Service Implementation for managing {@link Transaction}.
 */
@Service
@Transactional
public class TransactionService {

    private final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final LeaseRepository leaseRepository;
    private final TenantRepository tenantRepository;

    public TransactionService(
        TransactionRepository transactionRepository,
        UserRepository userRepository,
        LeaseRepository leaseRepository,
        TenantRepository tenantRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
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
        validate(transaction);

        if (transaction.getBookingDate() == null) {
            transaction.setBookingDate(Instant.now());
        }

        if (transaction.getLease() != null) {
            setBalanceAfter(transaction);
            assertBalanceNotNegative(transaction);
            updateBalanceInLaterTransactions(transaction);
        }

        return transactionRepository.save(transaction);
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

    @Transactional(readOnly = true)
    public List<Transaction> findAll() {
        log.debug("Request to get all Transactions");
        return transactionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findAll(Pageable pageable) {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(tenantRepository::findOneByUser)
            .map(Tenant::getLease)
            .map(lease -> findAll(lease, pageable))
            .orElse(Page.empty());
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findAll(Lease lease, Pageable pageable) {
        return transactionRepository.findAllByLeaseOrderByValueDateDesc(lease, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findAllForTenant(Pageable pageable) {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(tenantRepository::findOneByUser)
            .map(tenant -> findAllForTenant(tenant, pageable))
            .orElse(Page.empty());
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findAllForTenant(Tenant tenant, Pageable pageable) {
        return transactionRepository.findAllByTenantOrderByValueDateDesc(tenant, pageable);
    }

    @Transactional(readOnly = true)
    public BigDecimal getCurrentBalance() {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .map(this::getCurrentBalance)
            .orElse(BigDecimal.ZERO);
    }

    @Transactional(readOnly = true)
    public BigDecimal getCurrentBalance(User user) {
        return tenantRepository.findOneByUser(user)
            .map(this::getCurrentBalance)
            .orElse(BigDecimal.ZERO);
    }

    @Transactional(readOnly = true)
    public BigDecimal getCurrentBalance(Tenant tenant) {
        return leaseRepository.findOneByTenants(tenant)
            .map(this::getCurrentBalance)
            .orElse(BigDecimal.ZERO);
    }

    @Transactional(readOnly = true)
    public BigDecimal getCurrentBalance(Lease lease) {
        return getBalanceOn(lease, Instant.now());
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalanceOn(Lease lease, Instant time) {
        return transactionRepository.findFirstByLeaseBefore(lease, time, PageRequest.of(0, 1))
            .stream()
            .map(Transaction::getBalanceAfter)
            .findFirst()
            .orElse(BigDecimal.ZERO);
    }

    private BigDecimal getBalanceOnWithLock(Lease lease, Instant time) {
        return transactionRepository.findFirstByLeaseBeforeWithLock(lease, time, PageRequest.of(0, 1))
            .stream()
            .map(Transaction::getBalanceAfter)
            .findFirst()
            .orElse(BigDecimal.ZERO);
    }

    private void setBalanceAfter(Transaction transaction) {
        BigDecimal lastBalance = getBalanceOnWithLock(transaction.getLease(), transaction.getValueDate());
        BigDecimal newBalance = lastBalance.add(transaction.getValue());
        transaction.setBalanceAfter(newBalance);
    }

    private void updateBalanceInLaterTransactions(Transaction transaction) {
        List<Transaction> laterTransactions = transactionRepository.findAllNewerThanWithLock(transaction.getLease(), transaction.getValueDate());
        BigDecimal balance = transaction.getBalanceAfter();
        for (Transaction t : laterTransactions) {
            balance = balance.add(t.getValue());
            t.setBalanceAfter(balance);
        }
        transactionRepository.saveAll(laterTransactions);
    }

    private void assertBalanceNotNegative(Transaction transaction) {
        if (BigDecimalUtil.isNegative(transaction.getBalanceAfter())) {
            throw new InsufficientFundsException();
        }
    }

    private void validate(Transaction transaction) {
        if (transaction.getLease() != null) {
            if (transaction.getBalanceAfter() != null) {
                throw new IllegalArgumentException("Balance for lease transactions must be assigned by TransactionService");
            }
        } else {
            if (transaction.getBalanceAfter() == null) {
                throw new IllegalArgumentException("Balance for non-lease transactions must be calculated in advance");
            }
        }
        if (transaction.getValueDate() == null) {
            throw new IllegalArgumentException("Value date must be set");
        }
        if (transaction.getKind() == TransactionKind.CREDIT) {
            if (BigDecimalUtil.isNegative(transaction.getValue())) {
                throw new IllegalArgumentException(
                    String.format("Transaction kind %s requires the value not to be negative, but was %s", transaction.getKind(), transaction.getValue()));
            }
        } else if (transaction.getKind() == TransactionKind.DEBIT
            || transaction.getKind() == TransactionKind.FEE
            || transaction.getKind() == TransactionKind.PURCHASE) {
            if (BigDecimalUtil.isPositive(transaction.getValue())) {
                throw new IllegalArgumentException(
                    String.format("Transaction kind %s requires the value not to be positive, but was %s", transaction.getKind(), transaction.getValue()));
            }
        }
    }
}

package de.farue.autocut.service;

import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.User;
import de.farue.autocut.repository.LeaseRepository;
import de.farue.autocut.repository.TenantRepository;
import de.farue.autocut.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service Implementation for managing {@link Transaction}.
 */
@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final LeaseRepository leaseRepository;
    private final TenantRepository tenantRepository;

    public TransactionService(TransactionRepository transactionRepository, LeaseRepository leaseRepository,
                              TenantRepository tenantRepository) {
        this.transactionRepository = transactionRepository;
        this.leaseRepository = leaseRepository;
        this.tenantRepository = tenantRepository;
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
        transactionRepository.save(transaction);
    }
}

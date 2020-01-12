package de.farue.autocut.service;

import de.farue.autocut.domain.PaymentAccount;
import de.farue.autocut.repository.PaymentAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service Implementation for managing {@link PaymentAccount}.
 */
@Service
@Transactional
public class PaymentAccountService {

    private final Logger log = LoggerFactory.getLogger(PaymentAccountService.class);

    private final PaymentAccountRepository paymentAccountRepository;

    public PaymentAccountService(PaymentAccountRepository paymentAccountRepository) {
        this.paymentAccountRepository = paymentAccountRepository;
    }

    /**
     * Save a paymentAccount.
     *
     * @param paymentAccount the entity to save.
     * @return the persisted entity.
     */
    public PaymentAccount save(PaymentAccount paymentAccount) {
        log.debug("Request to save PaymentAccount : {}", paymentAccount);
        return paymentAccountRepository.save(paymentAccount);
    }

    /**
     * Get all the paymentAccounts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PaymentAccount> findAll() {
        log.debug("Request to get all PaymentAccounts");
        return paymentAccountRepository.findAll();
    }



    /**
    *  Get all the paymentAccounts where Lease is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true) 
    public List<PaymentAccount> findAllWhereLeaseIsNull() {
        log.debug("Request to get all paymentAccounts where Lease is null");
        return StreamSupport
            .stream(paymentAccountRepository.findAll().spliterator(), false)
            .filter(paymentAccount -> paymentAccount.getLease() == null)
            .collect(Collectors.toList());
    }

    /**
     * Get one paymentAccount by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PaymentAccount> findOne(Long id) {
        log.debug("Request to get PaymentAccount : {}", id);
        return paymentAccountRepository.findById(id);
    }

    /**
     * Delete the paymentAccount by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete PaymentAccount : {}", id);
        paymentAccountRepository.deleteById(id);
    }
}

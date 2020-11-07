package de.farue.autocut.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.domain.Apartment;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionBookType;
import de.farue.autocut.repository.LeaseRepository;
import de.farue.autocut.service.accounting.TransactionBookService;

/**
 * Service Implementation for managing {@link Lease}.
 */
@Service
@Transactional
public class LeaseService {

    private final Logger log = LoggerFactory.getLogger(LeaseService.class);

    private final LeaseRepository leaseRepository;

    private final ApartmentService apartmentService;

    private final TransactionBookService transactionBookService;

    public LeaseService(LeaseRepository leaseRepository, ApartmentService apartmentService,
        TransactionBookService transactionBookService) {
        this.leaseRepository = leaseRepository;
        this.apartmentService = apartmentService;
        this.transactionBookService = transactionBookService;
    }

    /**
     * Save a lease.
     *
     * @param lease the entity to save.
     * @return the persisted entity.
     */
    public Lease save(Lease lease) {
        log.debug("Request to save Lease : {}", lease);
        return leaseRepository.save(lease);
    }

    /**
     * Get all the leases.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Lease> findAll() {
        log.debug("Request to get all Leases");
        return leaseRepository.findAllWithEagerRelationships();
    }


    /**
     * Get all the leases with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Lease> findAllWithEagerRelationships(Pageable pageable) {
        return leaseRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one lease by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Lease> findOne(Long id) {
        log.debug("Request to get Lease : {}", id);
        return leaseRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the lease by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Lease : {}", id);
        leaseRepository.deleteById(id);
    }

    public Lease createNewLease(String apartmentValue, LocalDate leaseStart, LocalDate leaseEnd) {
        Apartment apartment = apartmentService.findByStudierendenwerkNumber(apartmentValue)
            .orElseThrow(() -> new ApartmentNotFoundException(apartmentValue));

        Lease newLease = new Lease()
            .start(leaseStart)
            .end(leaseEnd.plus(1, ChronoUnit.DAYS))
            .apartment(apartment)
            .nr(apartmentValue);
        log.debug("Created new lease: {}", newLease);
        return newLease;
    }

    public Optional<Lease> findByStudierendenwerkNumber(String apartmentString) {
        return findByStudierendenwerkNumber(apartmentString, Instant.now());
    }

    public Optional<Lease> findByStudierendenwerkNumber(String apartmentString, Instant date) {
        return apartmentService.findByStudierendenwerkNumber(apartmentString)
            .stream()
            .flatMap(apartment -> leaseRepository.findAllByApartmentAndDate(apartment, date).stream())
            .filter(lease -> StringUtils.equals(lease.getNr(), apartmentString))
            .findFirst();

    }

    public List<Lease> findByApartment(Apartment apartment) {
        return leaseRepository.findAllByApartmentAndDate(apartment, Instant.now());
    }

    public List<Lease> findByApartment(Apartment apartment, Instant date) {
        return leaseRepository.findAllByApartmentAndDate(apartment, date);
    }

    public TransactionBook getCashTransactionBook(Lease lease) {
        if (lease.getId() == null) {
            throw new IllegalStateException("Lease must be persisted first");
        }

        Lease loadedLease = leaseRepository.findOneWithEagerRelationships(lease.getId())
            .orElseThrow(() -> new IllegalArgumentException("Supplied lease does not exist"));

        return loadedLease.getTransactionBooks().stream()
            .filter(book -> book.getType() == TransactionBookType.CASH)
            .findFirst()
            .orElseGet(() -> {
                TransactionBook newTransactionBook = transactionBookService.save(new TransactionBook().type(TransactionBookType.CASH));
                loadedLease.addTransactionBook(newTransactionBook);
                save(loadedLease);
                return newTransactionBook;
            });
    }

    public TransactionBook getDepositTransactionBook(Lease lease) {
        if (lease.getId() == null) {
            throw new IllegalStateException("Lease must be persisted first");
        }

        Lease loadedLease = leaseRepository.findOneWithEagerRelationships(lease.getId())
            .orElseThrow(() -> new IllegalArgumentException("Supplied lease does not exist"));

        return loadedLease.getTransactionBooks().stream()
            .filter(book -> book.getType() == TransactionBookType.DEPOSIT)
            .findFirst()
            .orElseGet(() -> {
                TransactionBook newTransactionBook = transactionBookService.save(new TransactionBook().type(TransactionBookType.DEPOSIT));
                loadedLease.addTransactionBook(newTransactionBook);
                save(loadedLease);
                return newTransactionBook;
            });
    }

    // NB: Not read-only as a new transaction book is created if none exists
    public BigDecimal getCurrentCashBalance(Lease lease) {
        return transactionBookService.getCurrentBalance(getCashTransactionBook(lease));
    }

    // NB: Not read-only as a new transaction book is created if none exists
    public BigDecimal getCurrentDepositBalance(Lease lease) {
        return transactionBookService.getCurrentBalance(getDepositTransactionBook(lease));
    }
}

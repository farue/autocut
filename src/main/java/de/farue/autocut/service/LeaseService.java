package de.farue.autocut.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.domain.Apartment;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.repository.LeaseRepository;

/**
 * Service Implementation for managing {@link Lease}.
 */
@Service
@Transactional
public class LeaseService {

    private final Logger log = LoggerFactory.getLogger(LeaseService.class);

    private final LeaseRepository leaseRepository;

    private final ApartmentService apartmentService;

    public LeaseService(LeaseRepository leaseRepository, ApartmentService apartmentService) {
        this.leaseRepository = leaseRepository;
        this.apartmentService = apartmentService;
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
        return leaseRepository.findAll();
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
        return leaseRepository.findById(id);
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

    public Lease createLeaseIfNotExists(String apartmentValue, Instant leaseStart, Instant leaseEnd) {
        return findByStudierendenwerkNumber(apartmentValue)
            .orElseGet(() -> {
                Apartment apartment = apartmentService.findByStudierendenwerkNumber(apartmentValue)
                    .orElseThrow(() -> new ApartmentNotFoundException(apartmentValue));

                Lease newLease = new Lease()
                    .start(leaseStart)
                    .end(leaseEnd)
                    .apartment(apartment)
                    .nr(apartmentValue);
                log.debug("Created new lease: {}", newLease);
                return newLease;
            });
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
}

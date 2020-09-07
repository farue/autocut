package de.farue.autocut.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public LeaseService(LeaseRepository leaseRepository) {
        this.leaseRepository = leaseRepository;
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
}

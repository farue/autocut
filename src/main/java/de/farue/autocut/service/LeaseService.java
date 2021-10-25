package de.farue.autocut.service;

import de.farue.autocut.domain.Lease;
import de.farue.autocut.repository.LeaseRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * Partially update a lease.
     *
     * @param lease the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Lease> partialUpdate(Lease lease) {
        log.debug("Request to partially update Lease : {}", lease);

        return leaseRepository
            .findById(lease.getId())
            .map(existingLease -> {
                if (lease.getNr() != null) {
                    existingLease.setNr(lease.getNr());
                }
                if (lease.getStart() != null) {
                    existingLease.setStart(lease.getStart());
                }
                if (lease.getEnd() != null) {
                    existingLease.setEnd(lease.getEnd());
                }
                if (lease.getBlocked() != null) {
                    existingLease.setBlocked(lease.getBlocked());
                }
                if (lease.getPictureContract() != null) {
                    existingLease.setPictureContract(lease.getPictureContract());
                }
                if (lease.getPictureContractContentType() != null) {
                    existingLease.setPictureContractContentType(lease.getPictureContractContentType());
                }

                return existingLease;
            })
            .map(leaseRepository::save);
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
}

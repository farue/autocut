package de.farue.autocut.web.rest;

import de.farue.autocut.domain.Lease;
import de.farue.autocut.repository.LeaseRepository;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; 
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link de.farue.autocut.domain.Lease}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LeaseResource {

    private final Logger log = LoggerFactory.getLogger(LeaseResource.class);

    private static final String ENTITY_NAME = "lease";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LeaseRepository leaseRepository;

    public LeaseResource(LeaseRepository leaseRepository) {
        this.leaseRepository = leaseRepository;
    }

    /**
     * {@code POST  /leases} : Create a new lease.
     *
     * @param lease the lease to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new lease, or with status {@code 400 (Bad Request)} if the lease has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/leases")
    public ResponseEntity<Lease> createLease(@Valid @RequestBody Lease lease) throws URISyntaxException {
        log.debug("REST request to save Lease : {}", lease);
        if (lease.getId() != null) {
            throw new BadRequestAlertException("A new lease cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Lease result = leaseRepository.save(lease);
        return ResponseEntity.created(new URI("/api/leases/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /leases} : Updates an existing lease.
     *
     * @param lease the lease to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lease,
     * or with status {@code 400 (Bad Request)} if the lease is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lease couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/leases")
    public ResponseEntity<Lease> updateLease(@Valid @RequestBody Lease lease) throws URISyntaxException {
        log.debug("REST request to update Lease : {}", lease);
        if (lease.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Lease result = leaseRepository.save(lease);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lease.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /leases} : get all the leases.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of leases in body.
     */
    @GetMapping("/leases")
    public List<Lease> getAllLeases() {
        log.debug("REST request to get all Leases");
        return leaseRepository.findAll();
    }

    /**
     * {@code GET  /leases/:id} : get the "id" lease.
     *
     * @param id the id of the lease to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the lease, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/leases/{id}")
    public ResponseEntity<Lease> getLease(@PathVariable Long id) {
        log.debug("REST request to get Lease : {}", id);
        Optional<Lease> lease = leaseRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(lease);
    }

    /**
     * {@code DELETE  /leases/:id} : delete the "id" lease.
     *
     * @param id the id of the lease to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/leases/{id}")
    public ResponseEntity<Void> deleteLease(@PathVariable Long id) {
        log.debug("REST request to delete Lease : {}", id);
        leaseRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}

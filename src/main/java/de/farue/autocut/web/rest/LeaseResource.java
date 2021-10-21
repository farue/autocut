package de.farue.autocut.web.rest;

import de.farue.autocut.domain.Lease;
import de.farue.autocut.repository.LeaseRepository;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.farue.autocut.domain.Lease}.
 */
@RestController
@RequestMapping("/api")
public class LeaseResource {

    private final Logger log = LoggerFactory.getLogger(LeaseResource.class);

    private static final String ENTITY_NAME = "lease";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LeaseService leaseService;

    private final LeaseRepository leaseRepository;

    public LeaseResource(LeaseService leaseService, LeaseRepository leaseRepository) {
        this.leaseService = leaseService;
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
        Lease result = leaseService.save(lease);
        return ResponseEntity
            .created(new URI("/api/leases/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /leases/:id} : Updates an existing lease.
     *
     * @param id the id of the lease to save.
     * @param lease the lease to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lease,
     * or with status {@code 400 (Bad Request)} if the lease is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lease couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/leases/{id}")
    public ResponseEntity<Lease> updateLease(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Lease lease)
        throws URISyntaxException {
        log.debug("REST request to update Lease : {}, {}", id, lease);
        if (lease.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lease.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!leaseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Lease result = leaseService.save(lease);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lease.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /leases/:id} : Partial updates given fields of an existing lease, field will ignore if it is null
     *
     * @param id the id of the lease to save.
     * @param lease the lease to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lease,
     * or with status {@code 400 (Bad Request)} if the lease is not valid,
     * or with status {@code 404 (Not Found)} if the lease is not found,
     * or with status {@code 500 (Internal Server Error)} if the lease couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/leases/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Lease> partialUpdateLease(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Lease lease
    ) throws URISyntaxException {
        log.debug("REST request to partial update Lease partially : {}, {}", id, lease);
        if (lease.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lease.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!leaseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Lease> result = leaseService.partialUpdate(lease);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lease.getId().toString())
        );
    }

    /**
     * {@code GET  /leases} : get all the leases.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of leases in body.
     */
    @GetMapping("/leases")
    public List<Lease> getAllLeases(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Leases");
        return leaseService.findAll();
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
        Optional<Lease> lease = leaseService.findOne(id);
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
        leaseService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

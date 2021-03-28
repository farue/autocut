package de.farue.autocut.web.rest;

import de.farue.autocut.domain.TenantCommunication;
import de.farue.autocut.repository.TenantCommunicationRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.farue.autocut.domain.TenantCommunication}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TenantCommunicationResource {

    private final Logger log = LoggerFactory.getLogger(TenantCommunicationResource.class);

    private static final String ENTITY_NAME = "tenantCommunication";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TenantCommunicationRepository tenantCommunicationRepository;

    public TenantCommunicationResource(TenantCommunicationRepository tenantCommunicationRepository) {
        this.tenantCommunicationRepository = tenantCommunicationRepository;
    }

    /**
     * {@code POST  /tenant-communications} : Create a new tenantCommunication.
     *
     * @param tenantCommunication the tenantCommunication to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tenantCommunication, or with status {@code 400 (Bad Request)} if the tenantCommunication has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tenant-communications")
    public ResponseEntity<TenantCommunication> createTenantCommunication(@Valid @RequestBody TenantCommunication tenantCommunication)
        throws URISyntaxException {
        log.debug("REST request to save TenantCommunication : {}", tenantCommunication);
        if (tenantCommunication.getId() != null) {
            throw new BadRequestAlertException("A new tenantCommunication cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TenantCommunication result = tenantCommunicationRepository.save(tenantCommunication);
        return ResponseEntity
            .created(new URI("/api/tenant-communications/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tenant-communications/:id} : Updates an existing tenantCommunication.
     *
     * @param id the id of the tenantCommunication to save.
     * @param tenantCommunication the tenantCommunication to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tenantCommunication,
     * or with status {@code 400 (Bad Request)} if the tenantCommunication is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tenantCommunication couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tenant-communications/{id}")
    public ResponseEntity<TenantCommunication> updateTenantCommunication(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TenantCommunication tenantCommunication
    ) throws URISyntaxException {
        log.debug("REST request to update TenantCommunication : {}, {}", id, tenantCommunication);
        if (tenantCommunication.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tenantCommunication.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tenantCommunicationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TenantCommunication result = tenantCommunicationRepository.save(tenantCommunication);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tenantCommunication.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tenant-communications/:id} : Partial updates given fields of an existing tenantCommunication, field will ignore if it is null
     *
     * @param id the id of the tenantCommunication to save.
     * @param tenantCommunication the tenantCommunication to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tenantCommunication,
     * or with status {@code 400 (Bad Request)} if the tenantCommunication is not valid,
     * or with status {@code 404 (Not Found)} if the tenantCommunication is not found,
     * or with status {@code 500 (Internal Server Error)} if the tenantCommunication couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tenant-communications/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<TenantCommunication> partialUpdateTenantCommunication(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TenantCommunication tenantCommunication
    ) throws URISyntaxException {
        log.debug("REST request to partial update TenantCommunication partially : {}, {}", id, tenantCommunication);
        if (tenantCommunication.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tenantCommunication.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tenantCommunicationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TenantCommunication> result = tenantCommunicationRepository
            .findById(tenantCommunication.getId())
            .map(
                existingTenantCommunication -> {
                    if (tenantCommunication.getSubject() != null) {
                        existingTenantCommunication.setSubject(tenantCommunication.getSubject());
                    }
                    if (tenantCommunication.getText() != null) {
                        existingTenantCommunication.setText(tenantCommunication.getText());
                    }
                    if (tenantCommunication.getNote() != null) {
                        existingTenantCommunication.setNote(tenantCommunication.getNote());
                    }
                    if (tenantCommunication.getDate() != null) {
                        existingTenantCommunication.setDate(tenantCommunication.getDate());
                    }

                    return existingTenantCommunication;
                }
            )
            .map(tenantCommunicationRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tenantCommunication.getId().toString())
        );
    }

    /**
     * {@code GET  /tenant-communications} : get all the tenantCommunications.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tenantCommunications in body.
     */
    @GetMapping("/tenant-communications")
    public List<TenantCommunication> getAllTenantCommunications() {
        log.debug("REST request to get all TenantCommunications");
        return tenantCommunicationRepository.findAll();
    }

    /**
     * {@code GET  /tenant-communications/:id} : get the "id" tenantCommunication.
     *
     * @param id the id of the tenantCommunication to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tenantCommunication, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tenant-communications/{id}")
    public ResponseEntity<TenantCommunication> getTenantCommunication(@PathVariable Long id) {
        log.debug("REST request to get TenantCommunication : {}", id);
        Optional<TenantCommunication> tenantCommunication = tenantCommunicationRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(tenantCommunication);
    }

    /**
     * {@code DELETE  /tenant-communications/:id} : delete the "id" tenantCommunication.
     *
     * @param id the id of the tenantCommunication to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tenant-communications/{id}")
    public ResponseEntity<Void> deleteTenantCommunication(@PathVariable Long id) {
        log.debug("REST request to delete TenantCommunication : {}", id);
        tenantCommunicationRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

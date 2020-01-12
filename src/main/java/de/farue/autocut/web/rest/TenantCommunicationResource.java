package de.farue.autocut.web.rest;

import de.farue.autocut.domain.TenantCommunication;
import de.farue.autocut.repository.TenantCommunicationRepository;
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
    public ResponseEntity<TenantCommunication> createTenantCommunication(@Valid @RequestBody TenantCommunication tenantCommunication) throws URISyntaxException {
        log.debug("REST request to save TenantCommunication : {}", tenantCommunication);
        if (tenantCommunication.getId() != null) {
            throw new BadRequestAlertException("A new tenantCommunication cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TenantCommunication result = tenantCommunicationRepository.save(tenantCommunication);
        return ResponseEntity.created(new URI("/api/tenant-communications/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tenant-communications} : Updates an existing tenantCommunication.
     *
     * @param tenantCommunication the tenantCommunication to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tenantCommunication,
     * or with status {@code 400 (Bad Request)} if the tenantCommunication is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tenantCommunication couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tenant-communications")
    public ResponseEntity<TenantCommunication> updateTenantCommunication(@Valid @RequestBody TenantCommunication tenantCommunication) throws URISyntaxException {
        log.debug("REST request to update TenantCommunication : {}", tenantCommunication);
        if (tenantCommunication.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TenantCommunication result = tenantCommunicationRepository.save(tenantCommunication);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tenantCommunication.getId().toString()))
            .body(result);
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
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}

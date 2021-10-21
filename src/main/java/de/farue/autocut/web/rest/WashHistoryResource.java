package de.farue.autocut.web.rest;

import de.farue.autocut.domain.WashHistory;
import de.farue.autocut.repository.WashHistoryRepository;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.farue.autocut.domain.WashHistory}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class WashHistoryResource {

    private final Logger log = LoggerFactory.getLogger(WashHistoryResource.class);

    private static final String ENTITY_NAME = "washHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WashHistoryRepository washHistoryRepository;

    public WashHistoryResource(WashHistoryRepository washHistoryRepository) {
        this.washHistoryRepository = washHistoryRepository;
    }

    /**
     * {@code POST  /wash-histories} : Create a new washHistory.
     *
     * @param washHistory the washHistory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new washHistory, or with status {@code 400 (Bad Request)} if the washHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/wash-histories")
    public ResponseEntity<WashHistory> createWashHistory(@RequestBody WashHistory washHistory) throws URISyntaxException {
        log.debug("REST request to save WashHistory : {}", washHistory);
        if (washHistory.getId() != null) {
            throw new BadRequestAlertException("A new washHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WashHistory result = washHistoryRepository.save(washHistory);
        return ResponseEntity
            .created(new URI("/api/wash-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /wash-histories/:id} : Updates an existing washHistory.
     *
     * @param id the id of the washHistory to save.
     * @param washHistory the washHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated washHistory,
     * or with status {@code 400 (Bad Request)} if the washHistory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the washHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/wash-histories/{id}")
    public ResponseEntity<WashHistory> updateWashHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody WashHistory washHistory
    ) throws URISyntaxException {
        log.debug("REST request to update WashHistory : {}, {}", id, washHistory);
        if (washHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, washHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!washHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        WashHistory result = washHistoryRepository.save(washHistory);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, washHistory.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /wash-histories/:id} : Partial updates given fields of an existing washHistory, field will ignore if it is null
     *
     * @param id the id of the washHistory to save.
     * @param washHistory the washHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated washHistory,
     * or with status {@code 400 (Bad Request)} if the washHistory is not valid,
     * or with status {@code 404 (Not Found)} if the washHistory is not found,
     * or with status {@code 500 (Internal Server Error)} if the washHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/wash-histories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WashHistory> partialUpdateWashHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody WashHistory washHistory
    ) throws URISyntaxException {
        log.debug("REST request to partial update WashHistory partially : {}, {}", id, washHistory);
        if (washHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, washHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!washHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WashHistory> result = washHistoryRepository
            .findById(washHistory.getId())
            .map(existingWashHistory -> {
                if (washHistory.getUsingDate() != null) {
                    existingWashHistory.setUsingDate(washHistory.getUsingDate());
                }
                if (washHistory.getReservationDate() != null) {
                    existingWashHistory.setReservationDate(washHistory.getReservationDate());
                }
                if (washHistory.getLastModifiedDate() != null) {
                    existingWashHistory.setLastModifiedDate(washHistory.getLastModifiedDate());
                }
                if (washHistory.getStatus() != null) {
                    existingWashHistory.setStatus(washHistory.getStatus());
                }

                return existingWashHistory;
            })
            .map(washHistoryRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, washHistory.getId().toString())
        );
    }

    /**
     * {@code GET  /wash-histories} : get all the washHistories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of washHistories in body.
     */
    @GetMapping("/wash-histories")
    public List<WashHistory> getAllWashHistories() {
        log.debug("REST request to get all WashHistories");
        return washHistoryRepository.findAll();
    }

    /**
     * {@code GET  /wash-histories/:id} : get the "id" washHistory.
     *
     * @param id the id of the washHistory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the washHistory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/wash-histories/{id}")
    public ResponseEntity<WashHistory> getWashHistory(@PathVariable Long id) {
        log.debug("REST request to get WashHistory : {}", id);
        Optional<WashHistory> washHistory = washHistoryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(washHistory);
    }

    /**
     * {@code DELETE  /wash-histories/:id} : delete the "id" washHistory.
     *
     * @param id the id of the washHistory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/wash-histories/{id}")
    public ResponseEntity<Void> deleteWashHistory(@PathVariable Long id) {
        log.debug("REST request to delete WashHistory : {}", id);
        washHistoryRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
